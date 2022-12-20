package me.func.mod.ui

import me.func.mod.reactive.ReactivePlace
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.pow

object ReactiveGlow : Listener {

    private val glowingPlaces = arrayListOf<ReactivePlace>()

    @EventHandler
    fun PlayerMoveEvent.handle() {
        if (to.x != from.x || to.y != from.y || to.z != from.z) {

            glowingPlaces.filter { it.subscribed.contains(player.uniqueId) }.forEach { place ->

                if ((place.x - to.x).pow(2) + (place.z - to.z).pow(2) <= place.radius.pow(2)) {
                    place.onEntire?.accept(player)
                }
            }
        }
    }

    @JvmStatic
    fun clearPlaces() {
        glowingPlaces.forEach { it.delete(it.subscribed.mapNotNull { uuid -> Bukkit.getPlayer(uuid) }.toSet()) }
        glowingPlaces.clear()
    }

    @JvmStatic
    fun ReactivePlace.link() {
        if (!glowingPlaces.contains(this)) glowingPlaces.add(this)
    }
}