package battlepass

import dev.xdark.clientapi.item.ItemStack

class BattlePage(
    @JvmField val items: List<ItemStack?> = listOf(),
    @JvmField val advancedItems: List<ItemStack?> = listOf(),
    @JvmField var exp: Int,
    @JvmField val requiredExp: Int,
    @JvmField val skipPrice: Int
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
