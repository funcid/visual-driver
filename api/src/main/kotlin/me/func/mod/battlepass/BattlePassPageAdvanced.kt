package me.func.mod.battlepass

import me.func.protocol.battlepass.BattlePassPage
import org.bukkit.inventory.ItemStack

data class BattlePassPageAdvanced(
    override var requiredExp: Int = 0,
    override var skipPrice: Int = 0,
    var items: List<ItemStack> = mutableListOf(),
    var advancedItems: List<ItemStack> = mutableListOf(),
) : BattlePassPage(requiredExp, skipPrice) {
    constructor(init: BattlePassPageAdvanced.() -> Unit) : this() { this.init() }
}
