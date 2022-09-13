package me.func.mod.conversation.broadcast

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object SubscribeVerifier : Listener {

    private val providers = arrayListOf<BroadcastSubscriber>()

    @EventHandler
    fun PlayerQuitEvent.handle() {
        providers.forEach { subscribeProvider -> subscribeProvider.removeSubscriber(player) }
        providers.removeIf { !it.isConstant && it.getSubscribersCount() == 0 }
    }

}