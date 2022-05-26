package me.func.mod.selection.queue

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.selection.MenuManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

inline fun queueView(setup: QueueView.() -> Unit) = QueueView().also(setup)

object QueueViewer : Listener {

    val views = hashMapOf<UUID, QueueView>()

    init {
        Anime.createReader("queue:leave") { player, _ ->
            views[player.uniqueId]?.onLeave?.accept(player)
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() { views.remove(player.uniqueId) }

    fun stop(vararg player: Player) = ModTransfer().send("queue:stop", *player)

}