import dev.xdark.clientapi.item.ItemStack

data class BattlePage(
    val index: Int,
    val items: List<ItemStack?> = listOf(),
    val advancedItems: List<ItemStack?> = listOf(),
    val exp: Int,
    val requiredExp: Int,
    val skipPrice: Int
)

fun getPage(pages: List<BattlePage>, expCurrent: Int): BattlePage? {
    var level = 1
    var exp = expCurrent
    for (page in pages) {
        if (exp >= page.requiredExp) {
            level++
            exp -= page.requiredExp
        } else {
            return BattlePage(page.index, page.items, page.advancedItems, exp, page.requiredExp, page.skipPrice)
        }
    }
    return null
}