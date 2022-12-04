package experimental

import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.render.DefaultVertexFormats
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import me.func.protocol.world.GlowingPlace
import me.func.protocol.data.color.Tricolor
import org.lwjgl.opengl.GL11
import readRgb
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import writeUuid
import java.util.UUID
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class GlowPlaces {
    companion object {
        private val places = arrayListOf<GlowingPlace>()
        private val placeCache = hashMapOf<UUID, MutableList<Triple<Double, Double, Double>>>()

        private val inside = hashSetOf<UUID>()

        init {
            mod.registerChannel("func:place") {
                val uuid = NetUtil.readId(this)
                places.add(
                    GlowingPlace(
                        uuid,
                        Tricolor(readInt(), readInt(), readInt()),
                        readDouble(),
                        readDouble(),
                        readDouble(),
                        readDouble(),
                        readInt()
                    )
                )
                placeCache.clear()
            }

            mod.registerChannel("func:place-clear") {
                places.clear()
                placeCache.clear()
            }

            mod.registerChannel("func:place-kill") {
                val uuid = NetUtil.readId(this)
                places.filter { it.uuid == uuid }.forEach { places.remove(it) }
            }

            mod.registerChannel("func:place-color") {
                val uuid = NetUtil.readId(this)
                val color = readRgb()
                places.filter { it.uuid == uuid }.forEach { it.rgb = color }
            }

            val mc = clientApi.minecraft()

            mod.registerHandler<RenderPass> {
                if (places.isEmpty())
                    return@registerHandler

                val entity = mc.renderViewEntity

                val pt = mc.timer.renderPartialTicks
                val prevX = entity.prevX
                val prevY = entity.prevY
                val prevZ = entity.prevZ

                GlStateManager.disableLighting()
                GlStateManager.disableTexture2D()
                GlStateManager.disableAlpha()
                GlStateManager.shadeModel(GL11.GL_SMOOTH)
                GlStateManager.enableBlend()
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
                GlStateManager.disableCull()
                GlStateManager.depthMask(false)

                places.sortedByDescending { place -> (place.x - entity.x).pow(2) + (place.z - entity.z).pow(2) }
                    .forEach { place ->

                        val distance = (place.x - entity.x).pow(2) + (place.z - entity.z).pow(2)

                        // если ладеко, просто не рисуем
                        if (distance > 100 * 100)
                            return@forEach

                        val isWasInside = inside.contains(place.uuid)
                        val isInside = distance <= place.radius

                        // если игрок перешел через место, отправить информацию на сервер
                        if (isWasInside != isInside) {

                            if (isWasInside) inside.remove(place.uuid)
                            else inside.add(place.uuid)

                            UIEngine.schedule(0.1) {
                                clientApi.clientConnection().sendPayload(
                                    "server:place-signal",
                                    Unpooled.buffer().writeUuid(place.uuid)
                                )
                            }
                        }

                        val x = place.x - (entity.x - prevX) * pt - prevX
                        val y = place.y - (entity.y - prevY) * pt - prevY
                        val z = place.z - (entity.z - prevZ) * pt - prevZ

                        val cache = placeCache[place.uuid] ?: arrayListOf()

                        val tessellator = clientApi.tessellator()
                        val bufferBuilder = tessellator.bufferBuilder

                        bufferBuilder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR)

                        val angles = place.angles


                        repeat(angles * 2 + 2) { index ->

                            if (cache.size <= index) {
                                cache.add(
                                    Triple(
                                        sin(Math.toRadians(index * 360.0 / angles / 2 - 1 + 45)) * place.radius,
                                        if (index % 2 == 1) 5.0 else 0.0,
                                        cos(Math.toRadians(index * 360.0 / angles / 2 - 1 + 45)) * place.radius
                                    )
                                )
                            }

                            val v3 = cache[index]

                            bufferBuilder.pos(x + v3.first, y + v3.second, z + v3.third)
                                .color(place.rgb.red, place.rgb.green, place.rgb.blue, if (index % 2 == 1) 0 else 100)
                                .endVertex()
                        }

                        tessellator.draw()
                    }

                GlStateManager.depthMask(true)
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR)
                GlStateManager.shadeModel(GL11.GL_FLAT)
                GlStateManager.enableTexture2D()
                GlStateManager.enableAlpha()
            }
        }
    }
}
