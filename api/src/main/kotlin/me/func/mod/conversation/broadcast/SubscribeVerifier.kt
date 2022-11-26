package me.func.mod.conversation.broadcast

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

object SubscribeVerifier : Listener {

    val providers = hashMapOf<UUID, PlayerSubscriber>()

    fun add(vararg playerSubscriber: PlayerSubscriber) = providers.putAll(playerSubscriber.associateBy { it.uuid })

    @EventHandler
    fun PlayerQuitEvent.handle() {

        providers.values.forEach { subscribeProvider -> subscribeProvider.removeSubscriber(player) }
        providers.filter { !it.value.isConstant && it.value.getSubscribersCount() == 0 }.forEach { providers.remove(it.key) }
    }

}