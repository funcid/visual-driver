package me.func.mod.progress

import me.func.mod.conversation.ModTransfer
import me.func.protocol.RGB
import me.func.protocol.math.Position
import me.func.protocol.progress.Progress
import org.bukkit.Location
import org.bukkit.entity.Player

class ReactiveProgress : Progress() {

    var lastUpdate = -1L

    private val subscribed = hashSetOf<Player>()

    // Отписать игроков от обновлений
    fun unsubscribe(vararg players: Player) = subscribed.removeAll(players)

    fun unsubscribe(players: Iterable<Player>) = subscribed.removeAll(players)

    fun send(players: List<Player>) = send(*players.toTypedArray())

    fun send(vararg players: Player) {

        // Отправить и добавить игроков в подписавшихся
        subscribed.addAll(players)
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
    fun delete(players: Set<Player> = subscribed) {

        // Удалить данный прогресс бар
        starter().send("progress-ui:remove", players)
        unsubscribe(players)
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

        subscribed.removeIf { !it.player.isOnline }

        transfer.send("progress-ui:update", subscribed)
    }

    private fun starter() = ModTransfer().uuid(uuid)

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(val model: ReactiveProgress = ReactiveProgress()) {

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
}