package me.func.mod.ui.token

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.mod.util.subscriber
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class TokenGroup(
    private var tokens: MutableList<Token>
) : PlayerSubscriber {

    constructor(vararg tokens: Token) : this(tokens.toMutableList())

    private var subscribers = hashSetOf<Player>()

    override var isConstant = true

    init {
        subscriber(this)

        Bukkit.getScheduler().runTaskTimer(Anime.provided, {

             tokens.forEach { it.update(subscribers.toList()) }
        }, 20, 20)
    }

    fun removeTokens(uuids: List<UUID>) {

        tokens.removeIf { uuids.contains(it.uuid) }
        ModTransfer()
            .integer(uuids.size)
            .apply {
                uuids.forEach { uuid(it) }
            }.send("token:remove-uuid", subscribers)
    }

    fun subscribe(player: Player) {

        subscribers.add(player)
        ModTransfer()
            .integer(tokens.size)
            .apply {
                tokens.forEach { token -> uuid(token.uuid) }
            }.send("token:add", player)
    }

    override fun removeSubscriber(player: Player) {
        subscribers.remove(player)
    }

    override fun getSubscribersCount() = subscribers.size

}