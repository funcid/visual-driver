package me.func.mod.battlepass

import me.func.protocol.battlepass.BattlePassPage
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.function.Function

data class BattlePassPageAdvanced(
    override var uuid: UUID = UUID.randomUUID(),
    override var requiredExp: Int = 0,
    var items: List<ItemStack> = mutableListOf(),
    var advancedItems: List<ItemStack> = mutableListOf(),
    override var quests: List<String> = mutableListOf(),
    var questStatusUpdater: Function<Player, List<String>>? = null
) : BattlePassPage(uuid, requiredExp, quests) {
    constructor(init: BattlePassPageAdvanced.() -> Unit) : this() { this.init() }
}
