package me.func.mod.ui

import me.func.mod.reactive.ReactivePlace
import me.func.mod.util.warn
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*
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
    fun get(uuid: UUID) = glowingPlaces.firstOrNull { it.uuid == uuid }

    @JvmStatic
    fun addPlace(place: ReactivePlace, vararg players: Player) {

        if (Glow.glowingPlaces.size > 300) {
            warn("Glow places map size > 300! Stop add glowing places!")
        }

        glowingPlaces.add(place)
        place.send(*players)
    }

    @JvmStatic
    fun removePlace(uuid: UUID) {

        val place = glowingPlaces.firstOrNull { it.uuid == uuid }
        if (place != null) removePlace(place)
    }

    @JvmStatic
    fun removePlace(place: ReactivePlace) {

        glowingPlaces.remove(place)
        place.delete(place.subscribed.mapNotNull { Bukkit.getPlayer(it) }.toSet())
    }

    @JvmStatic
    fun clearPlaces() {

        glowingPlaces.forEach { removePlace(it) }
        glowingPlaces.clear()
    }
}