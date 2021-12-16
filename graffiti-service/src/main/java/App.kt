import adapter.MongoAdapter
import io.netty.channel.Channel
import me.func.protocol.DropRare
import me.func.protocol.graffiti.Graffiti
import me.func.protocol.graffiti.GraffitiInfo
import me.func.protocol.graffiti.GraffitiPack
import me.func.protocol.graffiti.UserGraffitiData
import me.func.protocol.graffiti.packet.GraffitiBuyPackage
import me.func.protocol.graffiti.packet.GraffitiLoadUserPackage
import me.func.protocol.graffiti.packet.GraffitiUsePackage
import me.func.protocol.packet.DataPackage
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.MoneyTransactionRequestPackage
import ru.cristalix.core.network.packages.MoneyTransactionResponsePackage
import socket.ServerSocket
import socket.ServerSocketHandler
import java.util.*
import java.util.concurrent.CompletableFuture

const val PASSWORD = "PASSWORD"
lateinit var app: App

class App {
    val handlers: MutableMap<Class<out DataPackage>, PackageHandler<in DataPackage>> = mutableMapOf()
    lateinit var mongoAdapter: MongoAdapter

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            App().run()
        }
    }

    fun run() {
        app = this

        mongoAdapter = MongoAdapter(
            System.getenv("MONGO_URI"),
            System.getenv("MONGO_DB"),
            System.getenv("MONGO_COLLECTION")
        )

        MicroserviceBootstrap.bootstrap(MicroServicePlatform(2))

        val serverSocket = ServerSocket(System.getenv("GRAFFITI_SERVICE_PORT").toInt())
        serverSocket.start()

        registerHandler<GraffitiLoadUserPackage> { channel, _, pckg ->
            // Загрузка профиля игрока
            loadProfile(pckg.playerUuid) {
                pckg.data = it ?: UserGraffitiData(
                    pckg.playerUuid,
                    mutableListOf(GraffitiPack(UUID.fromString("307264a1-2c69-11e8-b5ea-1cb72caa35fd"), mutableListOf(
                        Graffiti(GraffitiInfo(UUID.fromString("307264a1-2c69-11e8-b5ea-1cb72caa35f1"), 0, 0, 128, 50), 50, "func"),
                        Graffiti(GraffitiInfo(UUID.fromString("307264a1-2c69-11e8-b5ea-1cb72caa35f2"), 128, 0, 128, 50), 50, "func"),
                        Graffiti(GraffitiInfo(UUID.fromString("307264a1-2c69-11e8-b5ea-1cb72caa35f3"), 256, 0, 128, 50), 50, "func"),
                        Graffiti(GraffitiInfo(UUID.fromString("307264a1-2c69-11e8-b5ea-1cb72caa35f4"), 0, 128, 128, 50), 50, "func"),
                    ), "Тест", "func", 999, DropRare.LEGENDARY.ordinal, true)), 0
                )

                // Если данные только что были сгенерированы - сохранить
                mongoAdapter.save(pckg.data!!)

                // Ответ серверу
                answer(channel, pckg)
            }
        }

        registerHandler<GraffitiBuyPackage> { channel, _, pckg ->
            // Загрузка профиля игрока
            loadProfile(pckg.playerUUID) { userData ->
                // Покупка граффити
                if (userData == null) {
                    answer(channel, pckg)
                } else {
                    // Если данные игрока успешно загружены
                    invoice(pckg.playerUUID, pckg.price, "Покупка граффити ${pckg.packUUID}").thenAccept { response ->
                        pckg.errorMessage = response.errorMessage

                        if (pckg.errorMessage.isNullOrEmpty()) {
                            // Если покупка прошла успешно
                            println("${pckg.playerUUID} payed ${pckg.price} for ${pckg.packUUID}!")

                            // Начисление граффити
                            userData.packs.filter { it.getUuid() == pckg.packUUID }
                                .forEach { pack ->
                                    pack.graffiti.forEach { it.uses += it.address.maxUses }
                                    println("${pckg.playerUUID} got ${pckg.packUUID} pack")
                                }

                            // Сохранение данных
                            mongoAdapter.save(userData)
                        }

                        // Отправка пакета назад
                        answer(channel, pckg)
                    }
                }
            }
        }

        registerHandler<GraffitiUsePackage> { channel, _, pckg ->
            // Загрузка профиля игрока
            loadProfile(pckg.playerUUID) { userData ->
                // Если данные игрока успешно загружены

                // Получение пака
                userData?.packs?.firstOrNull { it.getUuid() == pckg.packUUID }?.let { pack ->
                    pack.graffiti.firstOrNull { it.getUuid() == pckg.graffitiUUID && it.uses > 0 }?.let {
                        // Разрешить ставить граффити если оно есть
                        pckg.boolean = true

                        // Вычесть игроку одну штуку
                        it.uses--

                        // Сохранить изменение
                        mongoAdapter.save(userData)
                    }
                }

                // Ответ серверу об игроке
                answer(channel, pckg)
            }
        }
    }

    private fun loadProfile(uuid: UUID, accept: (UserGraffitiData?) -> (Any)) {
        mongoAdapter.find<UserGraffitiData>(uuid).exceptionally { null }.thenAccept { accept(it) }
    }

    private inline fun <reified T : DataPackage> registerHandler(packageHandler: PackageHandler<T>) {
        handlers[T::class.java] = packageHandler as PackageHandler<in DataPackage>
    }

    private fun answer(channel: Channel, pckg: DataPackage) {
        ServerSocketHandler.send(channel, pckg)
    }

    private fun invoice(user: UUID, price: Int, desc: String): CompletableFuture<MoneyTransactionResponsePackage> {
        return ISocketClient.get().writeAndAwaitResponse(MoneyTransactionRequestPackage(user, price, true, desc))
    }
}