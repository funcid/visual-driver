package me.func.mod.conversation.broadcast

import org.bukkit.entity.Player

interface BroadcastSubscriber {

    val isConstant: Boolean

    fun removeSubscriber(player: Player)

    fun getSubscribersCount(): Int

}