package me.func.mod.ui.menu.daily

import me.func.mod.reactive.ReactiveButton
import me.func.mod.ui.menu.MenuManager
import me.func.mod.ui.menu.MenuManager.bind
import me.func.mod.ui.menu.Storage
import org.bukkit.entity.Player
import java.util.*

class DailyRewardMenu(
    override var uuid: UUID = UUID.randomUUID(),
    override var storage: MutableList<ReactiveButton> = arrayListOf(),
    override var title: String = "",
    override var info: String = ""
) : Storage {

    override fun open(player: Player): Storage = MenuManager.push(player, this).apply { bind(player) }
}