import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.xdark.clientapi.entity.AbstractClientPlayer
import dev.xdark.clientapi.entity.PlayerModelPart.*
import me.func.protocol.npc.NpcData
import ru.cristalix.clientapi.JavaMod.clientApi
import java.util.*


object NpcManager {

    private val storage = mutableMapOf<UUID, NpcEntity>()
    private val wearing = arrayOf(CAPE, HAT, JACKET, LEFT_PANTS_LEG, LEFT_SLEEVE, RIGHT_PANTS_LEG, RIGHT_SLEEVE)

    fun spawn(data: NpcData) {
        val spawned = clientApi.entityProvider().newEntity(data.type, clientApi.minecraft().world).apply {
            entityId = data.id
            setUniqueId(data.uuid)
        } as AbstractClientPlayer

        val info = clientApi.clientConnection().newPlayerInfo(
            GameProfile(data.uuid, data.name).apply {
                properties.put("skinURL", Property("skinURL", data.skinUrl))
                properties.put("skinDigest", Property("skinDigest", data.skinDigest))
            }.apply { spawned.gameProfile = this }
        ).apply { responseTime = -2 }

        info.skinType = if (data.slimArms) "SLIM" else "DEFAULT"

        clientApi.clientConnection().addPlayerInfo(info)

        spawned.apply {
            wearing.forEach { setWearing(it) }
            alwaysRenderNameTag = true
            customNameTag = data.name

            teleport(data.x, data.y, data.z)
            setYaw(data.yaw)
            setPitch(data.pitch)

            setNoGravity(true)
        }

        storage[data.uuid] = NpcEntity(data.uuid, data, spawned)
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