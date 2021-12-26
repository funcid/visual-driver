package me.func.mod

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import me.func.mod.Npc.npcs
import me.func.mod.Npc.onClick
import me.func.mod.conversation.ModTransfer
import me.func.mod.data.NpcSmart
import me.func.protocol.npc.NpcData
import net.minecraft.server.v1_12_R1.ItemStack
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityTeleport
import net.minecraft.server.v1_12_R1.SoundEffects.id
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.EquipmentSlot
import sun.audio.AudioPlayer.player
import java.util.*
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