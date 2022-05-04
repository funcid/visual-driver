package experimental

import dev.xdark.clientapi.entity.Entity
import dev.xdark.clientapi.entity.EntityPlayer
import dev.xdark.clientapi.event.lifecycle.GameTickPre
import dev.xdark.clientapi.event.render.NameTemplateRender
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.UIEngine

object Disguise {
    private val minecraft = UIEngine.clientApi.minecraft()

    private val entityProvider = UIEngine.clientApi.entityProvider()

    private val players = hashMapOf<String, EntityPlayer>()
    private val disguised = hashMapOf<String, Entity>()

    private fun disguise() {
        disguised.forEach { players[it.key]?.renderingEntity = it.value }
    }

    init {
        Experimental::class.java.mod.run {
            minecraft.player.run { players.put(name, this) }

            registerHandler<NameTemplateRender> {
                if (entity is EntityPlayer) players[(entity as EntityPlayer).name] = entity as EntityPlayer
            }

            registerHandler<GameTickPre> { disguise() }

            registerChannel("anime:disguise") {
                try {
                    val world = minecraft.world

                    val playerName = NetUtil.readUtf8(this) // Player Name
                    val player = players[playerName] ?: return@registerChannel

                    if (readBoolean()) { // Reset
                        player.renderingEntity = player
                        disguised.remove(player.name)
                    } else {
                        val entityType = readShort() // Entity Type

                        disguised[player.name] = entityProvider.newEntity(entityType.toInt(), world)
                        disguise()
                    }
                } catch (_: Exception) {
                }
            }
        }
    }
}