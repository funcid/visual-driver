package me.func.mod.ui.scoreboard

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player

interface ScoreBoardRaw<T, S> {

    fun get(player: Player): ScoreBoardRecord<T, S>

    fun write(transfer: ModTransfer, player: Player) {
        val data = get(player)
        transfer.string(data.left.toString()).string(data.right.toString())
    }

}