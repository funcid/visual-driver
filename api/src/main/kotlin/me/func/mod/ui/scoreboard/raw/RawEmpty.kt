package me.func.mod.ui.scoreboard.raw

import me.func.mod.ui.scoreboard.ScoreBoardRaw
import me.func.mod.ui.scoreboard.ScoreBoardRecord
import org.bukkit.entity.Player

private val empty = ScoreBoardRecord("", "")

object RawEmpty : ScoreBoardRaw<String, String> {

    override fun get(player: Player) = empty

}