package me.func.mod

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import me.func.mod.data.NpcSmart
import me.func.protocol.npc.NpcData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.function.Consumer

object Npc {

    val npcs = mutableMapOf<Int, NpcSmart>()

    fun npc(init: NpcData.() -> Unit): NpcSmart {
        val data = NpcData()
        val npc = NpcSmart(data)

        npcs[data.id] = npc
        npc.data = data.apply(init)

        return npc
    }

    @JvmStatic
    fun npc(data: NpcData): NpcSmart {
        val npc = NpcSmart(data)
        npcs[data.id] = npc
        return npc
    }

    @JvmStatic
    fun clear() = Bukkit.getOnlinePlayers().forEach { Anime.sendEmptyBuffer("npc:kill-all", it) }

    @JvmStatic
    fun show(entityId: Int, player: Player) = npcs[entityId]?.show(player)

    @JvmStatic
    fun hide(entityId: Int, player: Player) = npcs[entityId]?.hide(player)

    @JvmStatic
    fun spawn(entityId: Int) = npcs[entityId]?.let { npc -> Bukkit.getOnlinePlayers().forEach { npc.show(it) } }

    @JvmStatic
    fun kill(entityId: Int) = npcs[entityId]?.let { npc -> Bukkit.getOnlinePlayers().forEach { npc.hide(it) } }

    fun NpcData.onClick(init: Consumer<PlayerUseUnknownEntityEvent>) {
        npcs[id]?.click = init
    }

    fun NpcData.location(location: Location) {
        x = location.x
        y = location.y
        z = location.z
        yaw = location.yaw
        pitch = location.pitch
    }
}