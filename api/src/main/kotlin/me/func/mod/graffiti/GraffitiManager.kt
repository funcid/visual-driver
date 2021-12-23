package me.func.mod.graffiti

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.protocol.graffiti.GraffitiPlaced
import me.func.protocol.graffiti.UserGraffitiData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.cristalix.core.formatting.Formatting
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.math.abs

object GraffitiManager {

    private const val GRAFFITI_TICKS_ALIVE = 20 * 30
    private const val MAX_GRAFFITI_IN_WORLD = 50
    private const val MAX_GRAFFITI_PER_PLAYER = 3
    private const val MAX_PLACE_DISTANCE = 10

    private val graffiti: HashMap<UUID, UserGraffitiData> = hashMapOf()
    private val placed: MutableList<GraffitiPlaced> = mutableListOf()

    fun sendGraffitiBulk(player: Player) {
        // Загрузка всех активных граффити за раз, в мире игрока
        val needed = placed.filter { player.world.name == it.world }
        val transfer = ModTransfer().integer(needed.size)

        needed.forEach { place ->
            transfer.string(place.owner.toString())
                .string(place.world)
                .string(place.graffiti.getUuid().toString())
                .integer(place.graffiti.address.x)
                .integer(place.graffiti.address.y)
                .integer(place.graffiti.address.size)
                .integer(place.graffiti.address.maxUses)
                .string(place.graffiti.author)
                .integer(place.graffiti.uses)
                .double(place.x)
                .double(place.y)
                .double(place.z)
                .double(place.rotationAngle)
                .double(place.rotationAxisX)
                .double(place.rotationAxisY)
                .double(place.rotationAxisZ)
                .integer(place.ticksLeft)
                .boolean(place.onGround)
                .boolean(place.local)
        }

        transfer.send("graffiti:create-bulk", player) // На моде все стоящие очистятся
    }

    fun clear(player: Player) {
        graffiti.remove(player.uniqueId)
    }

    fun safeRead(player: Player?): UserGraffitiData? {
        // Если игрок вышел и потом пришло сообщение
        if (player == null || !player.isOnline)
            return null
        val data = graffiti[player.uniqueId]

        // Если игрока нет в загруженных
        if (data == null) {
            player.sendMessage(Formatting.error("Ваши данные о граффити не прогружены, попробуйте перезайти."))
            return null
        }

        return data
    }

    init {
        // Временной цикл вычитающий оставшееся время граффити и у
        Bukkit.getScheduler().runTaskTimer(Anime.provided, {
            placed.removeIf { --it.ticksLeft < 0 }
        }, 100, 1)

        // С мода пришла попытка поставить граффити
        Anime.createReader("graffiti:use") { player, buffer ->

            val data = safeRead(player) ?: return@createReader
            val packUuid = Anime.safeReadUUID(buffer) ?: return@createReader

            // Если указанный пак вообще существует
            val pack = data.packs.find { it.getUuid() == packUuid }

            // Если указанное граффити есть у игрока
            if (pack == null) {
                player.sendMessage(Formatting.error("Этого пака граффити не существует."))
                return@createReader
            }

            // Поставить актуальный пак
            data.activePack = data.packs.indexOf(pack)

            // Если указанное граффити существует
            val graffitiUuid = Anime.safeReadUUID(buffer) ?: return@createReader
            val graffiti = pack.graffiti.firstOrNull { it.getUuid() == graffitiUuid } ?: return@createReader

            // Если указанное граффити есть у игрока
            if (graffiti.uses < 1) {
                player.sendMessage(Formatting.error("У вас закончилось это граффити! Если это ошибка, перезайдите на сервер."))
                return@createReader
            }

            // Если в мире и так много граффити
            if (placed.size > MAX_GRAFFITI_IN_WORLD) {
                player.sendMessage(Formatting.error("На этом сервере поставлено очень много граффити! Дождитесь исчезновения некоторых..."))
                return@createReader
            }

            // Если на одного игрока много поставленных граффити
            if (placed.filter { it.owner == player.uniqueId }.size > MAX_GRAFFITI_PER_PLAYER) {
                player.sendMessage(Formatting.error("Вы поставили слишком много граффити!"))
                return@createReader
            }

            // Получение координат граффити
            val x = buffer.readDouble()
            val y = buffer.readDouble()
            val z = buffer.readDouble()

            // Если игрок далеко от этих координат
            if (abs(player.location.x - x) > MAX_PLACE_DISTANCE || abs(player.location.z - z) > MAX_PLACE_DISTANCE)
                return@createReader

            // Мы прошли все проверки теперь можно тратить граффити!
            Anime.graffitiClient?.use(player.uniqueId, packUuid, graffitiUuid)?.thenAccept { success ->
                if (!success) {
                    player.sendMessage(Formatting.error("У вас закончилось это граффити! Если это ошибка, перезайдите на сервер."))
                    return@thenAccept
                }
                // Если даже сервис говорит что все окей
                graffiti.uses--

                // Создание граффити в мире
                val placedGraffiti = GraffitiPlaced(
                    player.uniqueId,
                    player.world.name,
                    graffiti, x, y, z,
                    GRAFFITI_TICKS_ALIVE,
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readBoolean(),
                    buffer.readBoolean()
                )

                // Добавление граффити в список стоящих
                placed.add(placedGraffiti)

                // Отправка данных о граффити всем игрокам на сервере
                Bukkit.getOnlinePlayers().filter { it.world.name == player.world.name }
                    .forEach { Anime.graffiti(it, placedGraffiti) }
            }
        }

        // Попытка купить граффити с клиента
        Anime.createReader("graffiti:buy") { player, buffer ->

            val data = safeRead(player) ?: return@createReader
            val packUuid = Anime.safeReadUUID(buffer) ?: return@createReader

            // Если такого пака не существует
            val pack = data.packs.find { it.getUuid() == packUuid } ?: return@createReader

            // Попробовать купить пак граффити
            Anime.graffitiClient?.buy(player.uniqueId, packUuid, pack.price)?.thenAccept { error ->
                error?.let {
                    player.sendMessage(Formatting.error(it))
                    return@thenAccept
                }

                // Если получилось купить граффити
                pack.graffiti.forEach { it.uses += it.address.maxUses }

                // Отправка информации на мод об успешной покупке пака
                ModTransfer()
                    .string(packUuid.toString())
                    .send("graffiti:bought", player)
            }
        }
    }

    fun tryPutData(uuid: UUID): CompletableFuture<UserGraffitiData?> {
        val future = CompletableFuture<UserGraffitiData?>()
        val value = graffiti[uuid]

        // Если каким-то образом в мапе уже был данный игрок, то данные не грузить
        if (value != null) {
            future.complete(value)
        } else {
            Anime.graffitiClient?.loadUser(uuid)?.thenAccept { data ->
                // Если данные успешно загрузились
                if (data != null)
                    graffiti[uuid] = data
                future.complete(data)
            }
        }
        return future
    }
}