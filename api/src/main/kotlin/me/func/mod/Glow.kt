package me.func.mod

import me.func.mod.conversation.ModTransfer
import me.func.mod.util.warn
import me.func.protocol.GlowingPlace
import me.func.protocol.RGB
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.UUID
import java.util.function.Consumer
import kotlin.math.pow

object Glow : Listener {

    val glowingPlaces = mutableMapOf<UUID, GlowingPlace>()
    private val playerAccepter = mutableMapOf<UUID, Consumer<Player>>()

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
            .string(place.uuid.toString())
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
        if (glowingPlaces.containsKey(place.uuid)) {
            glowingPlaces.remove(place.uuid)
            playerAccepter.remove(place.uuid)

            val current = ModTransfer().string(place.uuid.toString())
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
}