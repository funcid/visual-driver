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
) : Storage {

    override var info: String = ""

    var taken: Boolean = false
    var currentDay: Int = 0

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private val dailyRewardMenu: DailyRewardMenu = DailyRewardMenu()

        fun taken(taken: Boolean) = apply { dailyRewardMenu.taken = taken }
        fun currentDay(currentDay: Int) = apply { dailyRewardMenu.currentDay = currentDay }
        fun uuid(uuid: UUID) = apply { dailyRewardMenu.uuid = uuid }
        fun rewards(storage: MutableList<ReactiveButton>) = apply { dailyRewardMenu.storage = storage }
        fun rewards(vararg storage: ReactiveButton) = apply { dailyRewardMenu.storage = storage.toMutableList() }
        fun build() = dailyRewardMenu
    }

    override fun open(player: Player): Storage = MenuManager.push(player, this).apply {

        bind(player)
            .integer(currentDay + 1)
            .boolean(taken)
            .integer(storage.size)
            .apply { storage.forEach { it.write(this) } }
            .send("func:weekly-reward", player)
    }
}