package me.func.mod.reactive

import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.mod.util.subscriber
import me.func.protocol.data.color.RGB
import me.func.protocol.data.color.Tricolor
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class ReactiveLine : PlayerSubscriber {

    override var uuid: UUID = UUID.randomUUID()

    init {
        subscriber(this)
    }

    override val isConstant: Boolean = false

    override fun removeSubscriber(player: Player) {
        unsubscribe(player)
    }

    override fun getSubscribersCount() = subscribed.size

    private fun transfer() = ModTransfer().uuid(uuid)

    private val subscribed = hashSetOf<Player>()

    // Отписать игроков от обновлений
    fun unsubscribe(vararg players: Player) = subscribed.removeAll(players)

    fun unsubscribe(players: Iterable<Player>) = subscribed.removeAll(players)

    fun send(players: List<Player>) = send(*players.toTypedArray())

    fun send(vararg players: Player) {

        // Отправить и добавить игроков в подписавшихся
        subscribed.addAll(players)

        if (to != null) {

            transfer()
                .boolean(origin != null)
                .rgb(color)
                .v3(to!!)
                .integer(viewDistance)
                .string(texture)
                .apply {

                    if (origin != null) {
                        v3(origin!!)
                    }

                }.send("tensess:add-line", *players)
        }
    }

    fun remove(players: List<Player>) = remove(players.toSet())

    @JvmOverloads
    fun remove(players: Set<Player> = subscribed) {

        // Удалить данный указатель
        unsubscribe(players)
        transfer().send("tensess:remove-line", players)
    }

    private fun update(transfer: ModTransfer) {

        // Обновить указатель у всех подписанных
        subscribed.removeIf { !it.player.isOnline }
        transfer.send("tensess:update-line", subscribed)
    }

    var color: RGB = Tricolor(0, 0, 0)
        set(value) {
            update(transfer().integer(1).rgb(value))
            field = value
        }

    var to: Location? = null
        set(value) {

            if (value != null) {
                update(transfer().integer(2).v3(value))
                field = value
            }
        }

    var origin: Location? = null
        set(value) {

            transfer().integer(3).boolean(value != null).apply {
                if (value != null) {
                    v3(value)
                }
            }.apply {
                update(this)
            }

            field = value
        }

    var viewDistance: Int = 15
        set(value) {
            update(transfer().integer(4).integer(value))
            field = value
        }

    var texture: String = "cache/animation:pointer_1.png"
        set(value) {
            update(transfer().integer(5).string(value))
            field = value
        }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(private val model: ReactiveLine= ReactiveLine()) {
        fun uuid(UUID: UUID) = apply { model.uuid = UUID }
        fun to(location: Location?) = apply { model.to = location }
        fun color(color: RGB) = apply { model.color = color }
        fun viewDistance(distance: Int) = apply { model.viewDistance = distance }
        fun texture(texture: String) = apply { model.texture = texture }
        fun origin(origin: Location?) = apply { model.origin = origin }

        fun build() = model
    }
}