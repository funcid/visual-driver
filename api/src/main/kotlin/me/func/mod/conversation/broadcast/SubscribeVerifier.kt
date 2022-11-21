package me.func.mod.conversation.broadcast

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object SubscribeVerifier : Listener {

    private val providers = hashSetOf<PlayerSubscriber>()

    fun add(vararg playerSubscriber: PlayerSubscriber) = providers.addAll(playerSubscriber)

    @EventHandler
    fun PlayerQuitEvent.handle() {
        providers.forEach { subscribeProvider -> subscribeProvider.removeSubscriber(player) }
        providers.removeIf { !it.isConstant && it.getSubscribersCount() == 0 }
    }

}