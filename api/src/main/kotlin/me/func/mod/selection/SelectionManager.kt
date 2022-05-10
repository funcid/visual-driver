package me.func.mod.selection

import dev.xdark.feder.NetUtil
import me.func.mod.Anime
import me.func.mod.util.warn
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

inline fun selection(setup: Selection.() -> Unit) = Selection().also(setup)

inline fun button(setup: Button.() -> Unit) = Button().also(setup)

object SelectionManager : Listener {

    val handleMap = hashMapOf<UUID, Selection>() // player uuid to selection

    init {
        Anime.createReader("storage:click") { player, buffer ->
            if (!player.isOnline || !handleMap.containsKey(player.uniqueId))
                return@createReader
            handleMap[player.uniqueId]?.let {
                try {
                    val uuid = UUID.fromString(NetUtil.readUtf8(buffer))
                    if (uuid != it.uuid) {
                        warn("Server side selection uuid verify error! Cheater: ${player.name}")
                        return@createReader
                    }
                    val index = buffer.readInt()
                    if (index < 0 || it.storage!!.size <= index) {
                        warn("Server side selection button id verify error! Cheater: ${player.name}")
                        return@createReader
                    }
                    it.storage?.let { storage ->
                        storage[index].let { button ->
                            button.onClick?.accept(index, button)
                        }
                    }
                } catch (exception: Throwable) {
                    warn("Player ${player.name} wrote wrong selection uuid!")
                }
            }
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        handleMap.remove(player.uniqueId)
    }

    @JvmStatic
    fun open(player: Player, selection: Selection) = selection.open(player)
}