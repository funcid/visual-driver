package me.func.mod.ui.menu.queue

import me.func.mod.Anime
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

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

    fun stop(vararg player: Player) = player.forEach { Anime.sendEmptyBuffer("queue:stop", it) }

}