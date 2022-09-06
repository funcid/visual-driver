package me.func.mod.menu.selection

import me.func.mod.Anime.provided
import me.func.mod.menu.Button
import me.func.mod.menu.MenuManager.open
import me.func.mod.service.Services.getBalanceData
import me.func.mod.service.Services.getDiscount
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.cristalix.core.coupons.ICouponsService

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
        getBalanceData(player).thenCombine(getDiscount(player)) { balanceData, sale ->
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
