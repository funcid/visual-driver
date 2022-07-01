package standard

import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Relative
import ru.cristalix.uiengine.utility.Rotation
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.WHITE
import ru.cristalix.uiengine.utility.rectangle
import kotlin.collections.set

class MarkerManager {

    private var holos: MutableMap<String, Context3D> = HashMap()

    init {
        mod.registerChannel("func:marker-new") {
            val uuid = NetUtil.readUtf8(this)
            val x = readDouble()
            val y = readDouble()
            val z = readDouble()
            val scale = readDouble()
            val texture = NetUtil.readUtf8(this)
            addHolo(uuid, x, y, z, scale, texture)
        }

        mod.registerChannel("func:marker-move") {
            val uuid = NetUtil.readUtf8(this)
            val x = readDouble()
            val y = readDouble()
            val z = readDouble()

            val seconds = readDouble()

            holos[uuid]?.animate(seconds) {
                offset.x = x
                offset.y = y
                offset.z = z
            }
        }


        mod.registerChannel("func:marker-kill") {
            val uuid = NetUtil.readUtf8(this)
            holos[uuid]?.let {
                UIEngine.worldContexts.remove(it)
            }
            holos.remove(uuid)
        }

        mod.registerChannel("func:clear") {
            holos.forEach { UIEngine.worldContexts.remove(it.value) }
            holos.clear()
        }

        mod.registerHandler<RenderTickPre> {
            val player = clientApi.minecraft().player
            val timer = clientApi.minecraft().timer
            val yaw =
                (player.rotationYaw - player.prevRotationYaw) * timer.renderPartialTicks + player.prevRotationYaw
            val pitch =
                (player.rotationPitch - player.prevRotationPitch) * timer.renderPartialTicks + player.prevRotationPitch

            holos.forEach {
                it.value.rotation = Rotation(-yaw * Math.PI / 180 + Math.PI, 0.0, 1.0, 0.0)
                it.value.children[0].rotation = Rotation(-pitch * Math.PI / 180, 1.0, 0.0, 0.0)
            }
        }
    }

    private fun addHolo(uuid: String, x: Double, y: Double, z: Double, scale: Double, texture: String) {
        val rect = rectangle {
            textureLocation = clientApi.resourceManager().getLocation("minecraft", texture)
            size = V3(scale, scale)
            origin = Relative.CENTER
            color = WHITE
            beforeRender = {
                GlStateManager.disableDepth()
            }
            afterRender = {
                GlStateManager.enableDepth()
            }
        }
        val context = Context3D(V3(x, y, z))
        context.addChild(rect)
        holos[uuid] = context
        UIEngine.worldContexts.add(context)
    }
}
