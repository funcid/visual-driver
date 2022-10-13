package me.func.mod.ui

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.warn
import me.func.protocol.data.color.RGB
import me.func.protocol.world.GlowingPlace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*
import java.util.function.Consumer
import kotlin.math.pow

object Glow : Listener {

    val glowingPlaces = hashMapOf<UUID, GlowingPlace>()
    private val playerAccepter = hashMapOf<UUID, Consumer<Player>>()

    @EventHandler
    fun PlayerMoveEvent.handle() {
        if (to.x != from.x || to.y != from.y || to.z != from.z) {
            playerAccepter.entries.forEach {
                val place = glowingPlaces[it.key]

                if (place != null && (place.x - to.x).pow(2) + (place.z - to.z).pow(2) <= place.radius.pow(2))
                    it.value.accept(player)
            }
        }
    }

    @JvmStatic
    fun set(player: Player, red: Int, blue: Int, green: Int, alpha: Double) {
        ModTransfer()
            .integer(red)
            .integer(blue)
            .integer(green)
            .double(alpha)
            .send("func:glow", player)
    }

    @JvmStatic
    fun set(player: Player, color: RGB) = set(player, color.red, color.blue, color.green, 1.0)

    @JvmStatic
    fun animate(player: Player, seconds: Double, red: Int, blue: Int, green: Int, alpha: Double) {
        ModTransfer()
            .double(seconds)
            .integer(red)
            .integer(blue)
            .integer(green)
            .double(alpha)
            .send("func:glow-short", player)
    }

    @JvmStatic
    fun animate(player: Player, seconds: Double, color: RGB, alpha: Double) {
        animate(player, seconds, color.red, color.blue, color.green, alpha)
    }

    @JvmStatic
    fun animate(player: Player, seconds: Double, red: Int, blue: Int, green: Int) {
        animate(player, seconds, red, blue, green, 1.0)
    }

    @JvmStatic
    fun animate(player: Player, seconds: Double, color: RGB) {
        animate(player, seconds, color.red, color.blue, color.green, 1.0)
    }

    @JvmOverloads
    @JvmStatic
    fun addPlace(
        rgb: RGB,
        x: Double,
        y: Double,
        z: Double,
        onJoin: (Player) -> Unit = {}
    ) = addPlace(GlowingPlace(UUID.randomUUID(), rgb, x, y, z), onJoin)

    @JvmStatic
    fun addPlace(place: GlowingPlace, onJoin: Consumer<Player>) =
        addPlace(place).also { playerAccepter[place.uuid] = onJoin }

    @JvmStatic
    fun addPlace(place: GlowingPlace): GlowingPlace {
        if (glowingPlaces.size > 300) {
            warn("Glow places map size > 300! Stop add glowing places!")
            return place
        }
        glowingPlaces[place.uuid] = place
        return place
    }

    @JvmStatic
    fun showPlace(player: Player, place: GlowingPlace) {
        ModTransfer()
            .uuid(place.uuid)
            .integer(place.rgb.red)
            .integer(place.rgb.blue)
            .integer(place.rgb.green)
            .double(place.x)
            .double(place.y)
            .double(place.z)
            .double(place.radius)
            .integer(place.angles)
            .send("func:place", player)
    }

    @JvmStatic
    fun showLoadedPlace(player: Player, uuid: UUID) {
        glowingPlaces[uuid]?.let { place -> showPlace(player, place) }
    }

    @JvmStatic
    fun showAllPlaces(player: Player) = glowingPlaces.values.forEach { showLoadedPlace(player, it.uuid) }

    @JvmStatic
    fun removePlace(place: GlowingPlace, vararg players: Player): GlowingPlace {
        if (glowingPlaces.remove(place.uuid) != null) {
            playerAccepter.remove(place.uuid)

            val current = ModTransfer().uuid(place.uuid)
            players.forEach { current.send("func:place-kill", it) }
        }
        return place
    }

    @JvmStatic
    fun clearPlaces(vararg players: Player) {
        players.forEach { Anime.sendEmptyBuffer("func:place-clear", it) }
        glowingPlaces.clear()
        playerAccepter.clear()
    }

    @JvmStatic
    fun changePlaceColor(place: GlowingPlace, rgb: RGB, vararg players: Player) {
        ModTransfer()
            .uuid(place.uuid)
            .integer(rgb.red)
            .integer(rgb.blue)
            .integer(rgb.green)
            .send("func:place-color", *players)
    }
}