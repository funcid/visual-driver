import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.render.DefaultVertexFormats
import dev.xdark.feder.NetUtil
import me.func.protocol.element.Figure
import me.func.protocol.element.FigureType
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_QUAD_STRIP
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.UIEngine
import java.util.*
import kotlin.math.cos
import kotlin.math.sin


object Figures {

    private val figures = arrayListOf<Figure>()

    init {
        App::class.mod.registerChannel("func:figure") {
            figures.add(
                Figure(
                    UUID.fromString(NetUtil.readUtf8(this)),
                    FigureType.values()[readInt()],
                    readDouble(),
                    readDouble(),
                    readDouble(),
                    readDouble(),
                    readInt(),
                    if (isReadable) NetUtil.readUtf8(this) else null
                )
            )
        }

        App::class.mod.registerChannel("func:figure-clear") {
            figures.clear()
        }

        App::class.mod.registerChannel("func:figure-kill") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            figures.filter { it.uuid == uuid }.forEach { figures.remove(it) }
        }

        val mc = UIEngine.clientApi.minecraft()

        ru.cristalix.clientapi.registerHandler<RenderPass> {
            if (figures.isEmpty())
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

            figures.forEach { figure ->
                val radius = figure.size
                val accuracy = 10.0

                val dx = figure.x - (entity.x - prevX) * pt - prevX
                val dy = figure.y - (entity.y - prevY) * pt - prevY
                val dz = figure.z - (entity.z - prevZ) * pt - prevZ

                var j: Int
                var i = 0
                while (i <= accuracy) {
                    val lat0: Double = Math.PI * (-0.5 + (i - 1).toDouble() / accuracy)
                    val z0 = sin(lat0)
                    val zr0 = cos(lat0)
                    val lat1: Double = Math.PI * (-0.5 + i.toDouble() / accuracy)
                    val z1 = sin(lat1)
                    val zr1 = cos(lat1)

                    val tessellator = JavaMod.clientApi.tessellator()
                    val bufferBuilder = tessellator.bufferBuilder
                    bufferBuilder.begin(GL_QUAD_STRIP, DefaultVertexFormats.POSITION_NORMAL)
                    j = 0
                    while (j <= accuracy) {
                        val lng: Double = 2.0 * Math.PI * (j - 1) / accuracy
                        val x = cos(lng)
                        val y = sin(lng)

                        bufferBuilder.normal((x * zr0).toFloat(), (y * zr0).toFloat(), z0.toFloat())
                            .pos(
                                dx + radius * x * zr0,
                                dy + radius * y * zr0,
                                dz + radius * z0,
                            ).endVertex()
                        bufferBuilder.normal((x * zr1).toFloat(), (y * zr1).toFloat(), z1.toFloat())
                            .pos(
                                dx + radius * x * zr1,
                                dy + radius * y * zr1,
                                dz + radius * z1,
                            ).endVertex()
                        j++
                    }
                    tessellator.draw()
                    i++
                }
            }

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR)
            GlStateManager.shadeModel(GL11.GL_FLAT)
            GlStateManager.enableTexture2D()
            GlStateManager.enableAlpha()
            GlStateManager.enableCull()
        }
    }
}