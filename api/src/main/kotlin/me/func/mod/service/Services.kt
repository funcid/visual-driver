package me.func.mod.service

import com.github.benmanes.caffeine.cache.Caffeine
import org.apache.logging.log4j.jul.LogManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.cristalix.core.CoreCredentials
import ru.cristalix.core.coupons.CouponsService
import ru.cristalix.core.coupons.ICouponsService
import ru.cristalix.core.invoice.BalanceData
import ru.cristalix.core.invoice.IInvoiceService
import ru.cristalix.core.invoice.InvoiceService
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.SocketClient
import ru.cristalix.core.realm.RealmId
import java.lang.System.getenv
import java.lang.reflect.Constructor
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

object Services {
    internal val socketClient: ISocketClient =
        ISocketClient.get() ?: SocketClient(Bukkit.getLogger()).apply {
            connect(
                getenv("TOWER_IP"),
                getenv("TOWER_PORT").toInt(),
                CoreCredentials::class.java.getDeclaredConstructor(
                    RealmId::class.java,
                    String::class.java,
                    String::class.java
                ).apply { isAccessible = true }.newInstance(
                    RealmId.of("ANIM", 600 + (Math.random() * 300).toInt()),
                    getenv("TOWER_LOGIN"),
                    getenv("TOWER_PASSWORD")
                )
            )
            waitForHandshake()
        }

    private val couponService: ICouponsService = CouponsService(socketClient)
    private val invoiceService: IInvoiceService = InvoiceService(socketClient)

    private val balanceDataCache = Caffeine.newBuilder()
        .maximumSize(150)
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .buildAsync<UUID, BalanceData> { key, _ -> invoiceService.getBalanceData(key) }

    private val discountCache = Caffeine.newBuilder()
        .maximumSize(150)
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .buildAsync<UUID, Int> { key, executor ->
            CompletableFuture.supplyAsync({ couponService.getDiscount(key) }, executor)
        }

    fun getBalanceData(player: Player): CompletableFuture<BalanceData> = balanceDataCache[player.uniqueId]

    fun getDiscount(player: Player): CompletableFuture<Int> = discountCache[player.uniqueId]
}
