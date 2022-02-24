package me.func.mod.experimental.disguise

import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.lang.IllegalStateException

/**
 * Not works!
 */
internal class PacketEntityDisguiser : EntityDisguiser {
    private val entityTypeField = PacketPlayOutSpawnEntityLiving::class.java.getDeclaredField("c").apply {
        isAccessible = true
    }

    private fun PacketPlayOutSpawnEntityLiving.setEntityType(type: EntityType) = entityTypeField.set(this, type.typeId)

    override fun disguise(entity: Entity, type: EntityType, vararg players: Player) {
        if (entity !is LivingEntity) throw java.lang.UnsupportedOperationException("сейчас можно ток livingentity менять тип")

        players.forEach {
            if (it !is CraftPlayer) throw IllegalStateException("игрок не инстанс CraftPlayer, wtf???")

            val conn = it.handle.playerConnection

            val pk = PacketPlayOutSpawnEntityLiving((entity as CraftLivingEntity).handle).apply {
                setEntityType(type)
            }

            conn.sendPacket(PacketPlayOutEntityDestroy(intArrayOf(entity.entityId)))
            if (entity is CraftPlayer) {
                it.handle.playerConnection.sendPacket(
                    PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity.handle
                    )
                )
            }
            conn.sendPacket(pk)
        }
    }

    override fun reset(entity: Entity, vararg players: Player) = TODO("Not yet implemented")
}