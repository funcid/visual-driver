package experimental.places

import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.render.DefaultVertexFormats
import io.netty.buffer.Unpooled
import java.util.*
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine.clientApi
import writeUuid
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

object PlaceManager {

    private val places = arrayListOf<ReactivePlace>()
    private val placeCache = hashMapOf<UUID, ArrayList<Triple<Double, Double, Double>>>()

    private val inside = hashSetOf<UUID>()

    fun put(reactiveGlow: ReactivePlace) {
        places.add(reactiveGlow)
        placeCache.clear()
    }

    fun cacheClear() = placeCache.clear()

    fun get(uuid: UUID) = places.firstOrNull { it.uuid == uuid }

    fun remove(uuid: UUID) = places.filter { it.uuid == uuid }.forEach { places.remove(it) }

    init {
        mod.registerHandler<RenderPass> {

            if (places.isEmpty())
                return@registerHandler

            val minecraft = clientApi.minecraft()
            val entity = minecraft.renderViewEntity

            val pt = minecraft.timer.renderPartialTicks
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

            places.sortedByDescending { place ->
                (place.location.x - entity.x).pow(2) + (place.location.z - entity.z).pow(2) }
                .forEach { place ->

                    val distance = (place.location.x - entity.x).pow(2) + (place.location.z - entity.z).pow(2)

                    // если далеко, просто не рисуем
                    if (distance > 100 * 100)
                        return@forEach

                    val isWasInside = inside.contains(place.uuid)
                    val isInside = distance <= place.radius * place.radius

                    // если игрок перешел через место, отправить информацию на сервер
                    if (isWasInside != isInside) {

                        if (isWasInside) inside.remove(place.uuid)
                        else inside.add(place.uuid)

                        clientApi.clientConnection().sendPayload(
                            "server:place-signal",
                            Unpooled.buffer().writeUuid(place.uuid)
                        )
                    }

                    val x = place.location.x - (entity.x - prevX) * pt - prevX
                    val y = place.location.y - (entity.y - prevY) * pt - prevY
                    val z = place.location.z - (entity.z - prevZ) * pt - prevZ

                    val cache = placeCache[place.uuid] ?: arrayListOf()

                    val tessellator = clientApi.tessellator()
                    val bufferBuilder = tessellator.bufferBuilder

                    bufferBuilder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR)//

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