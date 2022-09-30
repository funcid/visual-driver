package me.func.mod.reactive

import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.mod.util.subscriber
import me.func.protocol.data.color.RGB
import me.func.protocol.math.Position
import me.func.protocol.ui.progress.Progress
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class ReactiveProgress : Progress(), PlayerSubscriber {

    init {
        subscriber(this)
    }

    override val isConstant: Boolean = false

    var lastUpdate = -1L

    private val subscribed = hashSetOf<UUID>()

    // Отписать игроков от обновлений
    fun unsubscribe(vararg players: Player) = subscribed.removeAll(players.map { it.uniqueId }.toSet())

    fun unsubscribe(players: Iterable<Player>) = subscribed.removeAll(players.map { it.uniqueId }.toSet())

    fun unsubscribeByUUID(vararg uuids: UUID) = subscribed.removeAll(uuids.toSet())

    fun unsubscribeByUUID(uuids: Iterable<UUID>) = subscribed.removeAll(uuids.toSet())

    fun send(players: List<Player>) = send(*players.toTypedArray())

    fun send(vararg players: Player) {

        // Отправить и добавить игроков в подписавшихся
        subscribed.addAll(players.map { it.uniqueId })
        starter()
            .rgb(lineColor)
            .integer(position.ordinal)
            .boolean(hideOnTab)
            .double(offsetX)
            .double(offsetY)
            .double(offsetZ)
            .double(progress)
            .string(text)
            .send("progress-ui:create", *players)
    }

    @JvmOverloads
    fun delete(players: Set<UUID> = subscribed) {

        // Удалить данный прогресс бар
        starter().send("progress-ui:remove", players.map { Bukkit.getPlayer(uuid) })
        unsubscribeByUUID(players)
    }


    override var progress: Double = super.progress
        set(value) {
            update(starter().integer(0).double(value))
            field = value
        }

    override var text: String = super.text
        set(value) {
            update(starter().integer(1).string(value))
            field = value
        }

    private fun update(transfer: ModTransfer) {

        // Обновить прогресс у всех игроков
        lastUpdate = System.currentTimeMillis()

        subscribed.removeIf { Bukkit.getPlayer(it) == null }

        transfer.send("progress-ui:update", subscribed.map { Bukkit.getPlayer(it) })
    }

    private fun starter() = ModTransfer().uuid(uuid)

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(val model: ReactiveProgress = ReactiveProgress()) {

        fun uuid(UUID: UUID) = apply { model.uuid = UUID }
        fun position(position: Position) = apply { model.position = position }
        fun color(lineColor: RGB) = apply { model.lineColor = lineColor }
        fun text(text: String) = apply { model.text = text }
        fun offsetY(offsetY: Double) = apply { model.offsetY = offsetY }
        fun offsetX(offsetX: Double) = apply { model.offsetX = offsetX }
        fun offsetZ(offsetZ: Double) = apply { model.offsetZ = offsetZ }
        fun location(location: Location) = offsetX(location.x).offsetY(location.y).offsetZ(location.z)
        fun progress(progress: Double) = apply { model.progress = progress }
        fun hideOnTab(hideOnTab: Boolean) = apply { model.hideOnTab = hideOnTab }
        fun build() = model
    }

    override fun removeSubscriber(player: Player) {
        unsubscribe(player)
    }

    override fun getSubscribersCount() = subscribed.size
}