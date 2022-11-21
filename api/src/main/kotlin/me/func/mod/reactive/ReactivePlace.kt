package me.func.mod.reactive

import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.mod.ui.menu.MenuManager.reactive
import me.func.mod.util.subscriber
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.color.RGB
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer
import kotlin.math.pow

class ReactivePlace : PlayerSubscriber {

    override var isConstant = false
    private val subscribed = hashSetOf<UUID>()

    var uuid: UUID = UUID.randomUUID()
    var rgb: RGB = GlowColor.GREEN
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0
    var radius: Double = 1.3
    var angles: Int = 12
    var onEntire: Consumer<Player>? = null
    var onLeave: Consumer<Player>? = null

    var playersInside = hashSetOf<UUID>()

    private fun starter() = ModTransfer().uuid(uuid)

    override fun removeSubscriber(player: Player) {
        subscribed.remove(player.uniqueId)
        playersInside.remove(player.uniqueId)
    }

    override fun getSubscribersCount() = subscribed.size

    init {
        subscriber(this)
    }

    fun getPlayersInside() = subscribed.mapNotNull { Bukkit.getPlayer(it) }.filter { player ->
        (x - player.location.x).pow(2) + (z - player.location.z).pow(2) <= radius.pow(2)
    }

    private fun update(transfer: ModTransfer) = transfer.send(
        "func:place-update",
        subscribed.mapNotNull(Bukkit::getPlayer)
    )

    fun send(vararg players: Player) {

        // Отправить и добавить игроков в подписавшихся
        subscribed.addAll(players.map { it.uniqueId })
        starter()
            .rgb(rgb)
            .v3(x, y, z)
            .double(radius)
            .integer(angles)
            .send("func:place", *players)
    }

    @JvmOverloads
    fun delete(players: Set<Player> = subscribed.mapNotNull { Bukkit.getPlayer(it) }.toSet()) {

        // Удалить данный прогресс бар
        starter().send("func:place-kill", players)
        players.forEach { removeSubscriber(it) }
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(val model: ReactivePlace = ReactivePlace()) {

        fun uuid(UUID: UUID) = apply { model.uuid = UUID }
        fun rgb(rgb: RGB) = apply { model.rgb = rgb }
        fun y(y: Double) = apply { model.y = y }
        fun x(x: Double) = apply { model.x = x }
        fun z(z: Double) = apply { model.z = z }
        fun location(location: Location) = x(location.x).y(location.y).z(location.z)
        fun radius(radius: Double) = apply { model.radius = radius }
        fun angles(angles: Int) = apply { model.angles = angles }
        fun onEntire(accept: Consumer<Player>) = apply { model.onEntire = accept }
        fun onLeave(accept: Consumer<Player>) = apply { model.onLeave = accept }
        fun build() = model
    }

}
