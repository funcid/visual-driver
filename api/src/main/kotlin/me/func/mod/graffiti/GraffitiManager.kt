package me.func.mod.graffiti

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.mod.service.Services.socketClient
import me.func.protocol.graffiti.packet.GraffitiBuyPackage
import me.func.protocol.graffiti.packet.GraffitiLoadUserPackage
import me.func.protocol.graffiti.packet.GraffitiUsePackage
import me.func.protocol.personalization.FeatureUserData
import me.func.protocol.personalization.GraffitiPlaced
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import ru.cristalix.core.formatting.Formatting
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate
import kotlin.math.abs

object GraffitiManager : PlayerSubscriber {

    private const val GRAFFITI_TICKS_ALIVE = 20 * 60 * 10
    private const val MAX_GRAFFITI_IN_WORLD = 50
    private const val MAX_GRAFFITI_PER_PLAYER = 5
    private const val MAX_PLACE_DISTANCE = 10

    private val graffiti: HashMap<UUID, FeatureUserData> = hashMapOf()
    private val placed: MutableList<GraffitiPlaced> = arrayListOf()

    var isCanPlace: Predicate<Location> = Predicate { true }

    fun sendGraffitiBulk(player: Player) {
        // Загрузка всех активных граффити за раз, в мире игрока
        val needed = placed.filter { player.world.name == it.world }
        val transfer = ModTransfer().integer(needed.size)

        needed.forEach { place ->
            transfer.string(place.owner.toString())
                .string(place.ownerName)
                .string(place.world)
                .string(place.graffiti.uuid.toString())
                .integer(place.graffiti.address.x)
                .integer(place.graffiti.address.y)
                .integer(place.graffiti.address.size)
                .integer(place.graffiti.address.maxUses)
                .string(place.graffiti.author)
                .integer(place.graffiti.uses)
                .double(place.x)
                .double(place.y)
                .double(place.z)
                .integer(place.ticksLeft)
                .double(place.rotationAngle)
                .double(place.rotationAxisX)
                .double(place.rotationAxisY)
                .double(place.rotationAxisZ)
                .double(place.extraRotation)
                .boolean(place.onGround)
        }

        transfer.send("graffiti:create-bulk", player) // На моде все стоящие очистятся
    }

    private fun safeRead(player: Player?): FeatureUserData? {
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
            val pack = data.packs.find { it.uuid == packUuid }

            // Если указанное граффити есть у игрока
            if (pack == null) {
                player.sendMessage(Formatting.error("Этого пака граффити не существует."))
                return@createReader
            }

            // Поставить актуальный пак
            data.activePack = data.packs.indexOf(pack)

            // Если указанное граффити существует
            val graffitiUuid = Anime.safeReadUUID(buffer) ?: return@createReader
            val graffiti = pack.graffiti.firstOrNull { it.uuid == graffitiUuid } ?: return@createReader

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
            if (placed.count { it.owner == player.uniqueId } >= MAX_GRAFFITI_PER_PLAYER) {
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

            // Проверяем, разрешил ли сервер использовать граффити
            if (!isCanPlace.test(Location(player.world, x, y, z))) {
                player.sendMessage(Formatting.error("Данный сервер запретил вам ставить тут граффити."))
                return@createReader
            }

            // Мы прошли все проверки теперь можно тратить граффити!
            socketClient.writeAndAwaitResponse<GraffitiUsePackage>(GraffitiUsePackage(player.uniqueId, packUuid, graffitiUuid)).thenAccept { pckg ->
                if (!pckg.success) {
                    player.sendMessage(Formatting.error("У вас закончилось это граффити! Если это ошибка, перезайдите на сервер."))
                    return@thenAccept
                }
                // Если даже сервис говорит что все окей
                graffiti.uses--

                // Создание граффити в мире
                val placedGraffiti = GraffitiPlaced(
                    player.uniqueId,
                    player.name,
                    player.world.name,
                    graffiti, x, y, z,
                    GRAFFITI_TICKS_ALIVE,
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
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
            val pack = data.packs.find { it.uuid == packUuid } ?: return@createReader

            // Покупка граффити недоступна
            player.sendMessage(Formatting.error("А ты умный! Но тут серверная проверка 㬓"))
            return@createReader

            // Попробовать купить пак граффити
            socketClient.writeAndAwaitResponse<GraffitiBuyPackage>(GraffitiBuyPackage(player.uniqueId, packUuid, pack.price)).thenAccept { pckg ->
                pckg.errorMessage?.let {
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

    fun tryPutData(uuid: UUID): CompletableFuture<FeatureUserData?> {
        val future = CompletableFuture<FeatureUserData?>()
        val value = graffiti[uuid]

        // Если каким-то образом в мапе уже был данный игрок, то данные не грузить
        if (value != null) {
            future.complete(value)
        } else {
            socketClient.writeAndAwaitResponse<GraffitiLoadUserPackage>(GraffitiLoadUserPackage(uuid)).thenAccept { pckg ->
                // Если данные успешно загрузились
                val data = pckg.data

                if (data != null)
                    graffiti[uuid] = data
                future.complete(data)
            }
        }
        return future
    }

    override val isConstant = true

    override fun removeSubscriber(player: Player) { graffiti.remove(player.uniqueId) }

    override fun getSubscribersCount() = graffiti.size
    override val uuid: UUID = UUID.randomUUID()
}
