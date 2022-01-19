package me.func.mod.battlepass

import me.func.protocol.battlepass.BattlePassPage
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.function.Function

data class BattlePassPageAdvanced(
    override var requiredExp: Int = 0,
    var items: List<ItemStack> = mutableListOf(),
    var advancedItems: List<ItemStack> = mutableListOf(),
) : BattlePassPage(requiredExp) {
    constructor(init: BattlePassPageAdvanced.() -> Unit) : this() { this.init() }
}
