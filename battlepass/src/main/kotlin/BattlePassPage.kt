data class BattlePage(val index: Int, val exp: Int)

fun getPage(pages: List<BattlePage>, expCurrent: Int): BattlePage? {
    var level = 1
    var exp = expCurrent
    for (page in pages) {
        if (exp >= page.exp) {
            level++
            exp -= page.exp
        } else {
            return page
        }
    }
    return null
}