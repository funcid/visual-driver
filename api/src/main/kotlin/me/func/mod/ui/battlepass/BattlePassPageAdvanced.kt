package me.func.mod.ui.battlepass

import me.func.protocol.ui.battlepass.BattlePassPage
import org.bukkit.inventory.ItemStack

data class BattlePassPageAdvanced(
    override var requiredExp: Int = 0,
    override var skipPrice: Int = 0,
    var items: List<ItemStack> = arrayListOf(),
    var advancedItems: List<ItemStack> = arrayListOf(),
) : BattlePassPage(requiredExp, skipPrice) {

    constructor(init: BattlePassPageAdvanced.() -> Unit) : this() { this.init() }
}
