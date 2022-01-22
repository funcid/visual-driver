data class BattlePage(val index: Int, val exp: Int, val requiredExp: Int, val skipPrice: Int)

fun getPage(pages: List<BattlePage>, expCurrent: Int): BattlePage? {
    var level = 1
    var exp = expCurrent
    for (page in pages) {
        if (exp >= page.requiredExp) {
            level++
            exp -= page.requiredExp
        } else {
            return BattlePage(page.index, exp, page.requiredExp, page.skipPrice)
        }
    }
    return null
}