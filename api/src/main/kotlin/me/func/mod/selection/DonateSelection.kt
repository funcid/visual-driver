package me.func.mod.selection

import com.github.benmanes.caffeine.cache.Caffeine
import me.func.mod.Anime.provided
import me.func.mod.selection.MenuManager.open
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.cristalix.core.coupons.ICouponsService
import ru.cristalix.core.invoice.BalanceData
import ru.cristalix.core.invoice.IInvoiceService
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Selection, у которого valut - donate, и money устанавливается сам, Кристалики N,
 * а так же автоматически устанавливает sale для [Button] из [ICouponsService], можно отлючить -
 * ```withGlobalSale(false)```
 */
open class DonateSelection @JvmOverloads constructor(
    title: String = "Меню",
    hint: String = "Купить",
    withGlobalSale: Boolean = true,
    rows: Int = 3,
    columns: Int = 4,
    storage: MutableList<Button> = arrayListOf(),
) : Selection(
    title = title,
    money = "Кристалликов undefined",
    vault = "donate",
    hint = hint,
    rows = rows,
    columns = columns,
    storage = storage
) {
    private companion object {
        val couponService: ICouponsService = ICouponsService.get()
        val invoiceService: IInvoiceService = IInvoiceService.get()

        val balanceDataCache = Caffeine.newBuilder()
            .maximumSize(150)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .buildAsync<UUID, BalanceData> { key, _ -> invoiceService.getBalanceData(key) }

        val discountCache = Caffeine.newBuilder()
            .maximumSize(150)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .buildAsync<UUID, Int> { key, executor ->
                CompletableFuture.supplyAsync({ couponService.getDiscount(key) }, executor)
            }
    }

    final override var money: String
        get() = super.money
        set(_) = error("cannot set money in DonateSelection")

    final override var vault: String
        get() = super.vault
        set(_) = error("cannot set vault in DonateSelection")

    /**
     * Будет ли применяться скидка из [ICouponsService] для цен в [Button]
     */
    var withGlobalSale: Boolean = withGlobalSale
        @JvmSynthetic set

    /**
     * Будет ли применяться скидка из [ICouponsService] для цен в [Button]
     */
    fun withGlobalSale(sale: Boolean) {
        withGlobalSale = sale
    }

    /**
     * @param player Игрок, которому откроется это меню
     * @return Этот же DonateSelection
     */
    final override fun open(player: Player): DonateSelection {
        balanceDataCache.get(player.uniqueId)
            .thenCombine(discountCache.get(player.uniqueId)) { balanceData, sale ->
                Bukkit.getScheduler().runTask(provided) {
                    open(
                        player,
                        "storage:open",
                        customStorage = if (withGlobalSale) storage.map {
                            it.copy().sale(sale)
                        } else storage
                    ) {
                        string(vault)
                        string("Кристалики ${balanceData.crystals + balanceData.coins}")
                        string(hint)
                        integer(rows)
                        integer(columns)
                    }
                }
            }
        return this
    }
}
