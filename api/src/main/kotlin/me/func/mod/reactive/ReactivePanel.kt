package me.func.mod.reactive

import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.mod.util.subscriber
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.color.RGB
import org.bukkit.entity.Player
import java.util.*

class ReactivePanel : PlayerSubscriber {

    override var uuid: UUID = UUID.randomUUID()

    init {
        subscriber(this)
    }

    override val isConstant: Boolean = false

    private val subscribed = hashSetOf<Player>()

    // Отписать игроков от обновлений
    fun unsubscribe(vararg players: Player) = subscribed.removeAll(players)

    fun unsubscribe(players: Iterable<Player>) = subscribed.removeAll(players)

    fun send(players: List<Player>) = send(*players.toTypedArray())

    fun send(vararg players: Player) {

        // Отправить и добавить игроков в подписавшихся
        subscribed.addAll(players)
        starter()
            .string(text)
            .double(progress)
            .rgb(color)
            .send("func:panel-new", *players)
    }

    @JvmOverloads
    fun delete(players: Set<Player> = subscribed) {

        // Удалить данный прогресс бар
        starter().send("func:panel-remove", players)
        unsubscribe(players)
    }

    var progress: Double = 0.0
        set(value) {
            update(starter().integer(0).double(value))
            field = value
        }

    var text: String = ""
        set(value) {
            update(starter().integer(1).string(value))
            field = value
        }

    var color: RGB = GlowColor.BLUE
        set(value) {
            update(starter().integer(2).rgb(value))
            field = value
        }

    private fun update(transfer: ModTransfer) {

        // Обновить прогресс у всех игроков
        subscribed.removeIf { !it.player.isOnline }

        transfer.send("func:panel-update", subscribed)
    }

    private fun starter() = ModTransfer().uuid(uuid)

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(val model: ReactivePanel = ReactivePanel()) {

        fun uuid(UUID: UUID) = apply { model.uuid = UUID }
        fun color(color: RGB) = apply { model.color = color }
        fun text(text: String) = apply { model.text = text }
        fun progress(progress: Double) = apply { model.progress = progress }
        fun build() = model
    }

    override fun removeSubscriber(player: Player) {
        unsubscribe(player)
    }

    override fun getSubscribersCount() = subscribed.size
}