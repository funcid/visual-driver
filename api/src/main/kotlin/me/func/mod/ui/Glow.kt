package me.func.mod.ui

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.SubscribeVerifier
import me.func.mod.reactive.ReactivePlace
import me.func.mod.util.warn
import me.func.protocol.data.color.RGB
import me.func.protocol.data.color.Tricolor
import me.func.protocol.world.GlowingPlace
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.awt.Color.green
import java.awt.Color.red
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
    fun set(player: Player, red: Int, green: Int, blue: Int, alpha: Double) {
        ModTransfer()
            .integer(red)
            .integer(green)
            .integer(blue)
            .double(alpha)
            .send("func:glow", player)
    }

    @JvmStatic
    fun set(player: Player, color: RGB) = set(player, color.red, color.green, color.blue, 1.0)

    @JvmStatic
    @JvmOverloads
    fun animate(player: Player, seconds: Double, color: RGB, alpha: Double = 1.0) {
        ModTransfer()
            .double(seconds)
            .rgb(color)
            .double(alpha)
            .send("func:glow-short", player)
    }

    @JvmStatic
    @JvmOverloads
    @Deprecated(
        "Используйте метод с RGB",
        ReplaceWith("animate(player, seconds, color, 1.0)", "me.func.mod.ui.Glow.animate")
    )
    fun animate(player: Player, seconds: Double, red: Int, green: Int, blue: Int, alpha: Double = 1.0) =
        animate(player, seconds, Tricolor(red, green, blue), alpha)

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
    @Deprecated("Используйте ReactivePlace")
    fun addPlace(place: GlowingPlace, onJoin: Consumer<Player>) =
        addPlace(place).also { playerAccepter[place.uuid] = onJoin }

    @JvmStatic
    @Deprecated("Используйте ReactivePlace")
    fun addPlace(place: GlowingPlace): GlowingPlace {
        if (glowingPlaces.size > 300) {
            warn("Glow places map size > 300! Stop add glowing places!")
            return place
        }
        glowingPlaces[place.uuid] = place
        return place
    }

    @JvmStatic
    @Deprecated("Используйте ReactivePlace")
    fun showPlace(player: Player, place: GlowingPlace) {
        ModTransfer()
            .uuid(place.uuid)
            .rgb(place.rgb)
            .v3(place.x, place.y, place.z)
            .double(place.radius)
            .integer(place.angles)
            .send("func:place", player)
    }

    @JvmStatic
    @Deprecated("Используйте ReactivePlace")
    fun showLoadedPlace(player: Player, uuid: UUID) {
        glowingPlaces[uuid]?.let { place -> showPlace(player, place) }
    }

    @JvmStatic
    @Deprecated("Используйте ReactivePlace")
    fun showAllPlaces(player: Player) = glowingPlaces.values.forEach { showLoadedPlace(player, it.uuid) }

    @JvmStatic
    @Deprecated("Используйте ReactivePlace")
    fun removePlace(place: GlowingPlace, vararg players: Player): GlowingPlace {
        if (glowingPlaces.remove(place.uuid) != null) {
            playerAccepter.remove(place.uuid)

            val current = ModTransfer().uuid(place.uuid)
            players.forEach { current.send("func:place-kill", it) }
        }
        return place
    }

    @JvmStatic
    @Deprecated("Используйте ReactivePlace")
    fun clearPlaces(vararg players: Player) {
        players.forEach { Anime.sendEmptyBuffer("func:place-clear", it) }
        glowingPlaces.clear()
        playerAccepter.clear()
    }

    @JvmStatic
    @Deprecated("Используйте ReactivePlace")
    fun changePlaceColor(place: GlowingPlace, rgb: RGB, vararg players: Player) {
        ModTransfer()
            .uuid(place.uuid)
            .rgb(rgb)
            .send("func:place-color", *players)
    }
}