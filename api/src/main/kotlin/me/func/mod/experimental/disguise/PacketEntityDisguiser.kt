@file:Suppress("NAME_SHADOWING")

package me.func.mod.experimental.disguise

import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.EntityLiving
import net.minecraft.server.v1_12_R1.EntityPlayer
import net.minecraft.server.v1_12_R1.EntityTracker
import net.minecraft.server.v1_12_R1.EntityTypes
import net.minecraft.server.v1_12_R1.EnumItemSlot
import net.minecraft.server.v1_12_R1.IChunkProvider
import net.minecraft.server.v1_12_R1.MinecraftServer
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEffect
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntity
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving
import net.minecraft.server.v1_12_R1.World
import net.minecraft.server.v1_12_R1.WorldData
import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.Bukkit
import org.bukkit.World.*
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException
import net.minecraft.server.v1_12_R1.Entity as NMSEntity

/**
 * Скорее всего работает все, кроме ресета игроков
 * (не все ресетается, например игрок не будет гореть после дизгаса + ресета)
 */
internal class PacketEntityDisguiser : EntityDisguiser {
    private val fakeWorld = object : WorldServer(
        MinecraftServer.SERVER, null, WorldData(), 0, Environment.NORMAL, null
    ) {
        override fun n(): IChunkProvider? = null
        override fun isChunkLoaded(i: Int, j: Int, flag: Boolean) = false
        override fun getTracker(): EntityTracker? = null
    }

    private val disguised = hashMapOf<Entity, EntityType>()

    private val entityTypeCache = hashMapOf<EntityType, Pair<Class<out NMSEntity>, DataWatcher>>()

    override fun disguise(entity: Entity, type: EntityType, vararg players: Player) {
        val players = players.filter { it != entity }

        val et = entityTypeCache.getOrPut(type) {
            val clazz = try {
                EntityTypes.clsToTypeMap.filter { it.value.equals(type) }.keys.first()
            } catch (e: NoSuchElementException) {
                if (type == EntityType.PLAYER) EntityPlayer::class.java
                else Nothing::class.java
            }

            clazz to clazz.getDeclaredConstructor(World::class.java).newInstance(fakeWorld).datawatcher
        }

        val clazz = et.first
        val dw = et.second

        // TODO: one packet for all players???

        if (EntityPlayer::class.java.isAssignableFrom(clazz)) {
            players.forEach {

                val conn = (it as CraftPlayer).handle.playerConnection

                conn.sendPacket(PacketPlayOutEntityDestroy(intArrayOf(entity.entityId)))
                conn.sendPacket(PacketPlayOutNamedEntitySpawn((entity as CraftPlayer).handle))
                conn.sendPacket(PacketPlayOutEntityMetadata(entity.entityId, entity.handle.datawatcher, false))

                EnumItemSlot.values().run { copyOfRange(2, size) }.forEach { slot ->
                    conn.sendPacket(
                        PacketPlayOutEntityEquipment(
                            entity.entityId, slot, entity.handle.getEquipment(slot)
                        )
                    )
                }

                entity.handle.getEffects().forEach { effect ->
                    conn.sendPacket(PacketPlayOutEntityEffect(entity.entityId, effect))
                }

                // TODO: дописать восстановку стейта
            }
        } else if (EntityLiving::class.java.isAssignableFrom(clazz)) {
            players.forEach {
                val conn = (it as CraftPlayer).handle.playerConnection

                conn.sendPacket(PacketPlayOutEntityDestroy(intArrayOf(entity.entityId)))
                conn.sendPacket(PacketPlayOutSpawnEntityLiving((entity as CraftLivingEntity).handle).apply {
                    c = type.typeId.toInt()
                    m = dw
                })
            }
        } else {
            // https://wiki.vg/index.php?title=Entity_metadata&oldid=14048#Objects
            val n: Int = when (type) {
                EntityType.BOAT -> 1
                EntityType.DROPPED_ITEM -> 2
                EntityType.AREA_EFFECT_CLOUD -> 3
                EntityType.MINECART -> 10
                EntityType.PRIMED_TNT -> 50
                EntityType.ENDER_CRYSTAL -> 51
                EntityType.ARROW, EntityType.TIPPED_ARROW -> 60
                EntityType.SNOWBALL -> 61
                EntityType.EGG -> 62
                EntityType.FIREBALL -> 63
                /* TODO: FireCharge (blaze projectile) */
                EntityType.ENDER_PEARL -> 65
                EntityType.WITHER_SKULL -> 66
                EntityType.SHULKER_BULLET -> 67
                EntityType.LLAMA_SPIT -> 68
                EntityType.FALLING_BLOCK -> 70
                EntityType.ITEM_FRAME -> 71
                EntityType.ENDER_SIGNAL -> 72
                EntityType.LINGERING_POTION, EntityType.SPLASH_POTION -> 73
                EntityType.THROWN_EXP_BOTTLE -> 75
                EntityType.FIREWORK -> 76
                EntityType.LEASH_HITCH -> 77
                EntityType.ARMOR_STAND -> 78
                EntityType.EVOKER_FANGS -> 79
                EntityType.FISHING_HOOK -> 90
                EntityType.SPECTRAL_ARROW -> 91
                EntityType.DRAGON_FIREBALL -> 93
                else -> throw UnsupportedOperationException("WTF")
            }

            players.forEach {
                val conn = (it as CraftPlayer).handle.playerConnection

                conn.sendPacket(PacketPlayOutEntityDestroy(intArrayOf(entity.entityId)))
                conn.sendPacket(PacketPlayOutSpawnEntity((entity as CraftEntity).handle, n))
            }
        }

        disguised[entity] = type
    }

    override fun reset(entity: Entity, vararg players: Player) {
        // TODO: Full reset players
        disguised[entity]?.let { disguise(entity, it, *players) }
        disguised.remove(entity)
    }
}
