package me.func.mod.data

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import me.func.mod.conversation.ModTransfer
import me.func.protocol.npc.NpcData
import net.minecraft.server.v1_12_R1.ItemStack
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.EquipmentSlot.*
import java.util.*
import java.util.function.Consumer

data class NpcSmart(
    var data: NpcData,
    var click: Consumer<PlayerUseUnknownEntityEvent>? = null,
    var worldUuid: UUID? = null,
    private var leftArm: ItemStack? = null,
    private var rightArm: ItemStack? = null,
    private var head: ItemStack? = null,
    private var chest: ItemStack? = null,
    private var legs: ItemStack? = null,
    private var feet: ItemStack? = null,
) {

    private fun setSlot(slot: EquipmentSlot, itemStack: ItemStack) {
        when (slot) {
            OFF_HAND -> leftArm = itemStack
            HAND -> rightArm = itemStack
            HEAD -> head = itemStack
            CHEST -> chest = itemStack
            LEGS -> legs = itemStack
            FEET -> feet = itemStack
        }
    }

    fun setWorld(world: World) = apply { worldUuid = world.uid }

    fun setWorld(world: UUID) = apply { worldUuid = world }

    fun slot(slot: EquipmentSlot, itemStack: ItemStack): NpcSmart {
        setSlot(slot, itemStack)
        Bukkit.getOnlinePlayers().forEach { updateEquipment(slot, it) }
        return this
    }

    fun slot(slot: EquipmentSlot, itemStack: ItemStack, player: Player): NpcSmart {
        setSlot(slot, itemStack)
        updateEquipment(slot, player)
        return this
    }

    private fun updateEquipment(slot: EquipmentSlot, player: Player): NpcSmart {
        ModTransfer().string(data.uuid.toString()).integer(slot.ordinal).item(
            when (slot) {
                OFF_HAND -> leftArm
                HAND -> rightArm
                HEAD -> head
                CHEST -> chest
                LEGS -> legs
                FEET -> feet
            }!!
        ).send("npc:slot", player)
        return this
    }

    fun kill(): NpcSmart {
        Bukkit.getOnlinePlayers().forEach { ModTransfer().string(data.uuid.toString()).send("npc:kill", it) }
        return this
    }

    fun show(player: Player): NpcSmart {
        ModTransfer().string(data.uuid.toString()).send("npc:show", player)
        return this
    }

    fun hide(player: Player): NpcSmart {
        ModTransfer().string(data.uuid.toString()).send("npc:hide", player)
        return this
    }

    fun update(player: Player): NpcSmart {
        ModTransfer()
            .string(data.uuid.toString())
            .string(data.name ?: "")
            .double(data.x)
            .double(data.y)
            .double(data.z)
            .double(data.yaw.toDouble())
            .double(data.pitch.toDouble())
            .boolean(data.sitting)
            .boolean(data.sleeping)
            .boolean(data.sneaking)
            .send("npc:update", player)
        return this
    }

    fun swingArm(mainHand: Boolean, player: Player): NpcSmart {
        ModTransfer(mainHand).send("npc:kick", player)
        return this
    }

    fun spawn(player: Player): NpcSmart {
        ModTransfer()
            .integer(data.id)
            .string(data.uuid.toString())
            .double(data.x)
            .double(data.y)
            .double(data.z)
            .integer(data.type)
            .string(data.name ?: "")
            .integer(data.behaviour.ordinal)
            .double(data.yaw.toDouble())
            .double(data.pitch.toDouble())
            .string(data.skinUrl ?: "")
            .string(data.skinDigest ?: "")
            .boolean(data.slimArms)
            .boolean(data.sneaking)
            .boolean(data.sleeping)
            .boolean(data.sitting)
            .send("npc:spawn", player)
        return this
    }

}