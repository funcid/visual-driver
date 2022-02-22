@file:Suppress("UNCHECKED_CAST")

import adapter.MongoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.func.protocol.DropRare
import me.func.protocol.graffiti.Graffiti
import me.func.protocol.graffiti.GraffitiPack
import me.func.protocol.graffiti.FeatureUserData
import me.func.protocol.graffiti.packet.GraffitiBuyPackage
import me.func.protocol.graffiti.packet.GraffitiLoadUserPackage
import me.func.protocol.graffiti.packet.GraffitiUsePackage
import me.func.protocol.sticker.packet.StickersAvailablePackage
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.Capability
import ru.cristalix.core.network.CorePackage
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.MoneyTransactionRequestPackage
import ru.cristalix.core.network.packages.MoneyTransactionResponsePackage
import ru.cristalix.core.realm.RealmId
import java.util.UUID
import java.util.concurrent.CompletableFuture

lateinit var app: App

class App {
    private val scope = CoroutineScope(Dispatchers.Default)

    // TODO: actualStickers

    private val actualGraffitiPacks = mutableListOf(
        GraffitiPack(
            UUID.fromString("307264a1-2c69-11e8-b5ea-1cb72caa35fd"), mutableListOf(
                Graffiti("307264a1-2c69-11e8-b5ea-1cb72caa35f1", 0, 0, 219, "func"),
                Graffiti("307264a1-2c69-11e8-b5ea-1cb72caa35f2", 219, 0, 219, "func"),
                Graffiti("307264a1-2c69-11e8-b5ea-1cb72caa35f3", 219 * 2, 0, 219, "func"),
                Graffiti("307264a1-2c69-11e8-b5ea-1cb72caa35f4", 219 * 3, 0, 219, "func"),
                Graffiti("307264a1-2c69-11e8-b5ea-1cb72caa35f4", 219 * 4, 0, 219, "func"),
            ), "Тест", "func", 999, DropRare.LEGENDARY.ordinal, true
        ), GraffitiPack(
            UUID.fromString("307264a1-2c69-11e8-b5ea-1cb72caa35fa"), mutableListOf(
                Graffiti("30726433-2c69-11e8-b5ea-1cb72caa35f1", 0, 219, 219, "func"),
                Graffiti("30726431-2c69-11e8-b5ea-1cb72caa35f2", 219, 219, 219, "func"),
                Graffiti("30726433-2c61-11e8-b5ea-1cb72caa35f3", 219 * 2, 219, 219, "func"),
                Graffiti("30726433-2c69-21e8-b5ea-1cb72caa35f4", 219 * 3, 219, 219, "func"),
                Graffiti("30726433-2c69-13e8-b5ea-1cb72caa35f4", 219 * 4, 219, 219, "func"),
                Graffiti("30726433-2c69-1148-b5ea-1cb72caa35f4", 219 * 5, 219, 219, "func"),
                Graffiti("30726433-2c69-11e5-b5ea-1cb72caa35f4", 219 * 6, 219, 219, "func"),
            ), "Тест 2", "func", 999, DropRare.COMMON.ordinal, true
        ), GraffitiPack(
            UUID.fromString("307264a1-2c69-11e8-b5ea-1cb72caa35fb"), mutableListOf(
                Graffiti("307264a2-2c69-11e8-b5e1-1cb72caa35f1", 0, 219 * 2, 219, "func"),
                Graffiti("307264a2-2c69-11e8-b5ea-2cb72caa35f2", 219, 219 * 2, 219, "func"),
                Graffiti("307264a2-2c69-11e8-b5ea-13b72caa35f3", 219 * 2, 219 * 2, 219, "func"),
                Graffiti("307264a2-2c69-11e8-b5ea-1c472caa35f4", 219 * 3, 219 * 2, 219, "func"),
                Graffiti("307264a2-2c69-11e8-b5ea-1cb52caa35f4", 219 * 4, 219 * 2, 219, "func"),
                Graffiti("307264a2-2c69-11e8-b5ea-1c472caa35f4", 219 * 5, 219 * 2, 219, "func"),
                Graffiti("307264a2-2c69-11e8-b5ea-1cb52caa35f4", 219 * 6, 219 * 2, 219, "func"),
            ), "Тест 3", "func", 999, DropRare.EPIC.ordinal, true
        )
    )
    private lateinit var mongoAdapter: MongoAdapter

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runBlocking { App().run() }
    }

    suspend fun run() {
        app = this

        mongoAdapter = MongoAdapter(
            System.getenv("MONGO_URI"), System.getenv("MONGO_DB"), System.getenv("MONGO_COLLECTION")
        )

        MicroserviceBootstrap.bootstrap(MicroServicePlatform(2))

        ISocketClient.get().registerCapabilities(
            Capability.builder()
                .className(GraffitiBuyPackage::class.java.name)
                .notification(true)
                .build(),
            Capability.builder()
                .className(GraffitiUsePackage::class.java.name)
                .notification(true)
                .build(),
            Capability.builder()
                .className(GraffitiLoadUserPackage::class.java.name)
                .notification(true)
                .build(),
            Capability.builder()
                .className(StickersAvailablePackage::class.java.name)
                .notification(true)
                .build()
        )

        registerHandler<StickersAvailablePackage> { _, p ->
            p.list = mutableListOf()
            ISocketClient.get().write(p)
        }

        registerHandler<GraffitiLoadUserPackage> { _, pckg ->
            // Загрузка профиля игрока
            runBlocking {
                loadProfile(pckg.playerUuid) { data ->
                    // Если данные уже есть - обновляем набор паков, если нет - создаем новые
                    pckg.data = data?.apply {
                        data.packs.addAll(actualGraffitiPacks.filter { !this.packs.contains(it) })
                    } ?: FeatureUserData(
                        pckg.playerUuid,
                        actualGraffitiPacks,
                        activeSticker = null, // TODO: See #L33
                        stickers = mutableListOf(),
                        0
                    )

                    // Если данные только что были сгенерированы - сохранить
                    mongoAdapter.save(pckg.data!!)

                    // Ответ серверу
                    ISocketClient.get().write(pckg)
                }
            }
        }

        registerHandler<GraffitiBuyPackage> { _, pckg ->
            // Загрузка профиля игрока
            runBlocking {
                loadProfile(pckg.playerUUID) { userData ->
                    // Покупка граффити
                    if (userData == null) {
                        ISocketClient.get().write(pckg)
                    } else {
                        // Если данные игрока успешно загружены
                        invoice(pckg.playerUUID, pckg.price, "Покупка граффити ${pckg.packUUID}").thenAccept { response ->
                            pckg.errorMessage = response.errorMessage

                            if (pckg.errorMessage.isNullOrEmpty()) {
                                // Если покупка прошла успешно
                                println("${pckg.playerUUID} payed ${pckg.price} for ${pckg.packUUID}!")

                                // Начисление граффити
                                userData.packs.filter { it.uuid == pckg.packUUID }.forEach { pack ->
                                    pack.graffiti.forEach { it.uses += it.address.maxUses }
                                    println("${pckg.playerUUID} got ${pckg.packUUID} pack")
                                }

                                // Сохранение данных
                                scope.launch { mongoAdapter.save(userData) }
                            }

                            // Отправка пакета назад
                            ISocketClient.get().write(pckg)
                        }
                    }
                }
            }
        }

        registerHandler<GraffitiUsePackage> { channel, pckg ->
            // Загрузка профиля игрока
            runBlocking {
                loadProfile(pckg.playerUUID) { userData ->
                    // Если данные игрока успешно загружены

                    // Получение пака
                    userData?.packs?.firstOrNull { it.uuid == pckg.packUUID }?.let { pack ->
                        pack.graffiti.firstOrNull { it.uuid == pckg.graffitiUUID && it.uses > 0 }?.let {
                            // Разрешить ставить граффити если оно есть
                            pckg.success = true

                            // Вычесть игроку одну штуку
                            it.uses--

                            // Сохранить изменение
                            mongoAdapter.save(userData)
                        }
                    }

                    // Ответ серверу об игроке
                    ISocketClient.get().write(pckg)
                }
            }
        }
    }

    private suspend fun loadProfile(uuid: UUID, accept: suspend (FeatureUserData?) -> (Any)) {
        accept(mongoAdapter.find(uuid))
    }

    private inline fun <reified T : CorePackage> registerHandler(noinline packageHandler: (RealmId, T) -> Unit) {
        ISocketClient.get().addListener(T::class.java, packageHandler)
    }

    private fun invoice(user: UUID, price: Int, desc: String): CompletableFuture<MoneyTransactionResponsePackage> {
        return ISocketClient.get().writeAndAwaitResponse(MoneyTransactionRequestPackage(user, price, true, desc))
    }
}
