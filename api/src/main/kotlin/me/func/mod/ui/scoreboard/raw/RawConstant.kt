package me.func.mod.ui.scoreboard.raw

import me.func.mod.ui.scoreboard.ScoreBoardRaw
import me.func.mod.ui.scoreboard.ScoreBoardRecord
import org.bukkit.entity.Player

class RawConstant<T, S>(private val left: T, private val right: S) : ScoreBoardRaw<T, S> {

    override fun get(player: Player) = ScoreBoardRecord(left, right)

}