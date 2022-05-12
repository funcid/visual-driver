package battlepass

import dev.xdark.clientapi.item.ItemStack
import kotlin.math.abs

class BattlePage(
    val items: List<ItemStack?> = listOf(),
    val advancedItems: List<ItemStack?> = listOf(),
    var exp: Int,
    val requiredExp: Int,
    val skipPrice: Int
)

fun getPage(pages: List<BattlePage>, expCurrent: Int): Pair<BattlePage?, Int> {
    var exp = expCurrent
    var level = 1

    for (page in pages) {
        for (item in page.items) {
            if (exp >= page.requiredExp) {
                exp -= page.requiredExp
            } else {
                page.exp = exp
                return page to level
            }
            level++
        }
    }

    return pages.last() to pages.size * REWARDS_COUNT
}