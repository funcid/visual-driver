package experimental

import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.render.DefaultVertexFormats
import dev.xdark.feder.NetUtil
import me.func.protocol.world.GlowingPlace
import me.func.protocol.data.color.Tricolor
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.mod
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

class GlowPlaces {
    companion object {
        private val places = arrayListOf<GlowingPlace>()

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
            }

            mod.registerChannel("func:place-clear") {
                places.clear()
            }

            mod.registerChannel("func:place-kill") {
                val uuid = NetUtil.readId(this)
                places.filter { it.uuid == uuid }.forEach { places.remove(it) }
            }

            mod.registerChannel("func:place-color") {
                val uuid = NetUtil.readId(this)
                val color = Tricolor(readInt().apply { println(this) }, readInt().apply { println(this) }, readInt().apply { println(this) })
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
                GlStateManager.disableCull()
                GlStateManager.shadeModel(GL11.GL_SMOOTH)
                GlStateManager.enableBlend()
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)

                places.forEach { place ->
                    val tessellator = clientApi.tessellator()
                    val bufferBuilder = tessellator.bufferBuilder

                    bufferBuilder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR)

                    val angles = place.angles

                    repeat(angles * 2 + 2) {
                        bufferBuilder.pos(
                            place.x - (entity.x - prevX) * pt - prevX + sin(Math.toRadians(it * 360.0 / angles / 2 - 1 + 45)) * place.radius,
                            place.y - (entity.y - prevY) * pt - prevY + if (it % 2 == 1) 5f else 0f,
                            place.z - (entity.z - prevZ) * pt - prevZ + cos(Math.toRadians(it * 360.0 / angles / 2 - 1 + 45)) * place.radius
                        ).color(place.rgb.red, place.rgb.blue, place.rgb.green, if (it % 2 == 1) 0 else 100).endVertex()
                    }

                    tessellator.draw()
                }

                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR)
                GlStateManager.shadeModel(GL11.GL_FLAT)
                GlStateManager.enableTexture2D()
                GlStateManager.enableAlpha()
                GlStateManager.enableCull()
            }
        }
    }
}
