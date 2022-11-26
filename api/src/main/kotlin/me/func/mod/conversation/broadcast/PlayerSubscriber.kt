package me.func.mod.conversation.broadcast

import me.func.protocol.Unique
import org.bukkit.entity.Player

interface PlayerSubscriber : Unique {

    val isConstant: Boolean

    fun removeSubscriber(player: Player)

    fun getSubscribersCount(): Int

}