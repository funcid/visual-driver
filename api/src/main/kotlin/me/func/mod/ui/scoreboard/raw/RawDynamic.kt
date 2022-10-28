package me.func.mod.ui.scoreboard.raw

import me.func.mod.ui.scoreboard.ScoreBoardRaw
import me.func.mod.ui.scoreboard.ScoreBoardRecord
import org.bukkit.entity.Player
import java.util.function.Function

class RawDynamic<T, S>(var accept: Function<Player, ScoreBoardRecord<T, S>>) : ScoreBoardRaw<T, S> {

    override fun get(player: Player) = accept.apply(player)

}