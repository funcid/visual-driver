package npc

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.xdark.clientapi.entity.AbstractClientPlayer
import dev.xdark.clientapi.entity.PlayerModelPart
import dev.xdark.clientapi.math.BlockPos
import dev.xdark.clientapi.util.EnumFacing
import me.func.protocol.world.npc.NpcData
import ru.cristalix.clientapi.JavaMod.clientApi
import java.security.MessageDigest
import java.util.*

class NpcManager {
    private val storage = hashMapOf<UUID, NpcEntity>()
    private val wearing = arrayOf(
        PlayerModelPart.CAPE,
        PlayerModelPart.HAT,
        PlayerModelPart.JACKET,
        PlayerModelPart.LEFT_PANTS_LEG,
        PlayerModelPart.LEFT_SLEEVE,
        PlayerModelPart.RIGHT_PANTS_LEG,
        PlayerModelPart.RIGHT_SLEEVE
    )

    private val digitsLower =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    private fun sha1Hex(input: String): String {
        val data = MessageDigest.getInstance("SHA-1").digest(input.toByteArray(Charsets.UTF_8))

        val l = data.size
        val out = CharArray(l shl 1)
        // two characters form the hex value.
        var i = 0
        var j = 0
        while (i < 0 + data.size) {
            out[j++] = digitsLower[0xF0 and data[i].toInt() ushr 4]
            out[j++] = digitsLower[0x0F and data[i].toInt()]
            i++
        }
        return String(out)
    }

    fun spawn(data: NpcData): NpcEntity {
        val entity = clientApi.entityProvider().newEntity(data.type, clientApi.minecraft().world)
        entity.entityId = data.id
        val spawned = entity as AbstractClientPlayer

        val info = clientApi.clientConnection().newPlayerInfo(
            GameProfile(data.uuid, data.name).apply {
                if (data.skinValue?.isNotEmpty() == true && data.skinSignature?.isNotEmpty() == true) {
                    properties.put("textures", Property("textures", data.skinValue, data.skinSignature))
                } else {
                    properties.put("skinURL", Property("skinURL", data.skinUrl))
                    properties.put(
                        "skinDigest",
                        Property("skinDigest", data.skinDigest ?: sha1Hex(data.skinUrl ?: "null"))
                    )
                }
            }.apply { spawned.gameProfile = this }
        ).apply { responseTime = -2 }

        spawned.setUniqueId(info.gameProfile.id)

        spawned.apply {
            wearing.forEach { setWearing(it) }
//            alwaysRenderNameTag = true
            customNameTag = data.name

            teleport(data.x, data.y, data.z)
            rotationYawHead = (data.yaw / Math.PI * 180).toFloat()
            setYaw((data.yaw / Math.PI * 180).toFloat())
            setPitch(data.pitch)

            if (data.sitting)
                enableRidingAnimation()
            if (data.sleeping)
                enableSleepAnimation(BlockPos.of(data.x.toInt(), data.y.toInt(), data.z.toInt()), EnumFacing.DOWN)
            isSneaking = data.sneaking

            setNoGravity(true)
        }
        info.skinType = if (data.slimArms) "SLIM" else "DEFAULT"
        clientApi.clientConnection().addPlayerInfo(info)

        val npc = NpcEntity(data.uuid, data, spawned)

        storage[data.uuid] = npc

        return npc
    }

    fun get(uuid: UUID) = storage[uuid]

    fun show(uuid: UUID) = storage[uuid]?.let { clientApi.minecraft().world.spawnEntity(it.entity) }

    fun hide(uuid: UUID) = storage[uuid]?.let { clientApi.minecraft().world.removeEntity(it.entity) }

    fun each(function: (UUID, NpcEntity) -> Unit) {
        storage.forEach { (uuid, data) -> function(uuid, data) }
    }

    fun kill(uuid: UUID) {
        hide(uuid)
        storage.remove(uuid)
    }
}
