package me.func.mod.ui.scoreboard

import me.func.mod.conversation.ModTransfer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

data class ScoreBoardScheme(
    var key: String = "ERROR",
    var header: String = "Название",
    var footer: String = "cristalix.gg",
    val uuid: UUID = UUID.randomUUID(),
    var lastUpdateTimestamp: Long = System.currentTimeMillis(),
    var content: MutableList<ScoreBoardRaw<*, *>> = arrayListOf(),
) {

    private fun starter() = ModTransfer().uuid(uuid)

    fun bind(player: Player) {
        // Отправляем схему игрокам
        val transfer = starter().string(header).string(footer).integer(content.size)

        transfer.send("func:scoreboard-scheme", player)
    }

    private fun update(player: Player) {
        starter().apply {

            content.forEach { raw -> raw.write(this, player) }
        }.send("func:scoreboard-update", player)
    }

    private fun update(uuid: UUID) {
        update(Bukkit.getPlayer(uuid) ?: return)
    }

    fun show(player: Player) = ScoreBoard.subscribe(key, player)

    fun hide(player: Player) = ScoreBoard.hide(player)

    fun update(subscribers: Iterable<Player>) = subscribers.forEach { update(it) }

    fun updateByUUID(subscribers: Iterable<UUID>) = subscribers.forEach { update(Bukkit.getPlayer(it)) }

    fun update(vararg subscribers: Player) = subscribers.forEach { update(it) }

    fun updateByUUID(vararg subscribers: UUID) = subscribers.forEach { update(Bukkit.getPlayer(it)) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoreBoardScheme

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

}