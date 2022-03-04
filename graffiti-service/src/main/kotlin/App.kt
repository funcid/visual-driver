@file:Suppress("UNCHECKED_CAST")

import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import me.func.protocol.graffiti.packet.GraffitiBuyPackage
import me.func.protocol.graffiti.packet.GraffitiLoadUserPackage
import me.func.protocol.graffiti.packet.GraffitiUsePackage
import me.func.protocol.personalization.FeatureUserData
import me.func.protocol.personalization.packet.StickersAvailablePackage
import ru.cristalix.core.GlobalSerializers
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.Capability
import ru.cristalix.core.network.CorePackage
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.MoneyTransactionRequestPackage
import ru.cristalix.core.network.packages.MoneyTransactionResponsePackage
import ru.cristalix.core.realm.RealmId
import java.util.*

private val scope = CoroutineScope(Dispatchers.IO)
private lateinit var mongoAdapter: MongoAdapter

fun main() {
    mongoAdapter = MongoAdapter(
        System.getenv("MONGO_URI"), System.getenv("MONGO_DB"), System.getenv("MONGO_COLLECTION")
    )

    MicroserviceBootstrap.bootstrap(MicroServicePlatform(2))

    ISocketClient.get().registerCapabilities(
        Capability.builder().className(GraffitiBuyPackage::class.java.name).notification(true).build(),
        Capability.builder().className(GraffitiUsePackage::class.java.name).notification(true).build(),
        Capability.builder().className(GraffitiLoadUserPackage::class.java.name).notification(true).build(),
        Capability.builder().className(StickersAvailablePackage::class.java.name).notification(true).build()
    )

    registerHandler<StickersAvailablePackage> { _, packet ->
        // Ответ серверу списком доступных стикеров
        packet.list = mutableListOf()
        // TODO: Тут пропущена логика
        ISocketClient.get().write(packet)
    }

    val standardProfile = FeatureUserData(
        UUID.randomUUID(), actualGraffitiPacks.toMutableList(), 0, mutableListOf(), null
    )

    registerHandler<GraffitiLoadUserPackage> { realm, packet ->
        // Загрузка профиля игрока
        scope.launch {
            loadProfile(packet.playerUuid) { data ->
                // Если данные уже есть - обновляем набор паков, если нет - создаем новые
                packet.data = data?.apply {
                    // Добавление новых паков
                    packs.addAll(actualGraffitiPacks.filter { !packs.contains(it) }.map { it.clone() })
                } ?: standardProfile.apply {
                    uuid = packet.playerUuid

                    // Если данные только что были сгенерированы - сохранить
                    mongoAdapter.save(packet.data!!)
                }

                // Ответ серверу
                ISocketClient.get().write(packet)
                println("Wrote graffiti load to ${data?.uuid} from ${realm.realmName}.")
            }
        }
    }

    registerHandler<GraffitiBuyPackage> { realm, pckg ->
        // Загрузка профиля игрока
        scope.launch {
            loadProfile(pckg.playerUUID) { userData ->
                // Покупка граффити
                if (userData == null) {
                    ISocketClient.get().write(pckg)
                    println("Cannot buy pack! UserData is null.")
                } else {
                    // Если данные игрока успешно загружены
                    pckg.errorMessage = invoice(
                        pckg.playerUUID, pckg.price, "Покупка граффити ${pckg.packUUID}"
                    ).errorMessage

                    if (pckg.errorMessage.isNullOrEmpty()) {
                        // Если покупка прошла успешно
                        println("${pckg.playerUUID} payed ${pckg.price} for ${pckg.packUUID}!")

                        // Начисление граффити
                        actualGraffitiPacks.filter { it.uuid == pckg.packUUID }.forEach { pack ->
                            // Если у игрока нет этого пака - добавить
                            if (!userData.packs.contains(pack))
                                userData.packs.add(pack.clone())

                            // Выдача граффити
                            userData.packs.firstOrNull { pack.uuid == it.uuid }?.graffiti?.forEach { it.uses += it.address.maxUses }
                            println("${pckg.playerUUID} got ${pckg.packUUID} pack")
                        }

                        // Сохранение данных
                        async { mongoAdapter.save(userData) }.invokeOnCompletion {
                            if (it == null) println(
                                "Successful payment from ${realm.realmName}! ${pckg.playerUUID} bought pack ${
                                    pckg.packUUID
                                }"
                            ) else {
                                println("Payment save error")
                                it.printStackTrace()
                            }
                        }
                    }

                    // Отправка пакета назад
                    ISocketClient.get().write(pckg)
                }
            }
        }
    }

    registerHandler<GraffitiUsePackage> { realm, pckg ->
        // Загрузка профиля игрока
        scope.launch {
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
                        println("Player use graffiti from ${realm.realmName}! ${pckg.playerUUID} used ${pckg.packUUID}")
                    }
                }

                // Ответ серверу об игроке
                ISocketClient.get().write(pckg)
            }
        }
    }
}

private suspend fun loadProfile(uuid: UUID, accept: suspend (FeatureUserData?) -> (Any)) =
    accept(mongoAdapter.find(uuid) ?: FeatureUserData(uuid, mutableListOf(), 0, mutableListOf(), null))

private inline fun <reified T : CorePackage> registerHandler(noinline packageHandler: (RealmId, T) -> Unit) =
    ISocketClient.get().addListener(T::class.java, packageHandler)

private suspend fun invoice(user: UUID, price: Int, desc: String): MoneyTransactionResponsePackage =
    coroutineScope {
        async {
            ISocketClient.get().writeAndAwaitResponse<MoneyTransactionResponsePackage>(
                MoneyTransactionRequestPackage(user, price, true, desc)
            ).await()
        }
    }.await()
