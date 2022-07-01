package standard

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.xdark.clientapi.entity.AbstractClientPlayer
import dev.xdark.clientapi.entity.EntityProvider
import dev.xdark.clientapi.math.BlockPos
import dev.xdark.clientapi.util.EnumFacing
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import java.util.UUID

class CorpseManager {
    private val corpses: MutableList<AbstractClientPlayer> = arrayListOf()

    init {
        mod.registerChannel("func:corpse-clear") {
            corpses.forEach { clientApi.minecraft().world.removeEntity(it) }
            corpses.clear()
        }

        mod.registerChannel("func:corpse") {
            val name = NetUtil.readUtf8(this)

            if (corpses.size > 36)
                corpses.clear()

            val corpse = clientApi.entityProvider()
                .newEntity(EntityProvider.PLAYER, clientApi.minecraft().world) as AbstractClientPlayer

            val uuid = UUID.randomUUID()
            corpse.setUniqueId(uuid)

            val profile = GameProfile(uuid, name)
            profile.properties.put("skinURL", Property("skinURL", NetUtil.readUtf8(this)))
            profile.properties.put("skinDigest", Property("skinDigest", NetUtil.readUtf8(this)))
            corpse.gameProfile = profile

            val info = clientApi.clientConnection().newPlayerInfo(profile)
            info.responseTime = -2
            info.skinType = "DEFAULT"
            clientApi.clientConnection().addPlayerInfo(info)

            val x = readDouble()
            var y = readDouble()
            val z = readDouble()
            var counter = 0
            var id: Int
            do {
                y -= 0.15
                counter++
                id = clientApi.minecraft().world.getBlockState(x, y, z).id
            } while ((id == 0 || id == 171 || id == 96 || id == 167) && counter < 50)

            corpse.enableSleepAnimation(
                BlockPos.of(x.toInt(), y.toInt(), z.toInt()), when (Math.random()) {
                    in 0.0..0.2 -> EnumFacing.SOUTH
                    in 0.2..0.4 -> EnumFacing.DOWN
                    in 0.4..0.6 -> EnumFacing.EAST
                    else -> EnumFacing.NORTH
                }
            )
            corpse.teleport(x, y + 0.2, z)
            corpse.setNoGravity(false)

            corpses.add(corpse)

            UIEngine.schedule(readInt()) {
                corpses.remove(corpse)
                clientApi.minecraft().world.removeEntity(corpse)
            }

            clientApi.minecraft().world.spawnEntity(corpse)
        }
    }
}
