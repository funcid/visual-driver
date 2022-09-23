package standard.world

import dev.xdark.clientapi.entity.Entity
import dev.xdark.clientapi.event.render.NameTemplateRender
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.feder.NetUtil
import me.func.protocol.data.element.Banner
import me.func.protocol.data.element.MotionType
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class Banners {

    private val banners = hashMapOf<UUID, Pair<Banner, Context3D>>()
    private val sizes = hashMapOf<Pair<UUID, Int>, Double>()

    private fun toBlackText(string: String) =
        "¨222200" + string.replace(Regex("(§[0-9a-fA-F]|¨......)"), "¨222200")

    init {
        mod.registerChannel("banner:new") {
            repeat(readInt()) {
                val uuid = UUID.fromString(NetUtil.readUtf8(this))
                val banner = Banner(
                    uuid = uuid,
                    motionType = MotionType.values()[readInt()],
                    watchingOnPlayer = readBoolean(),
                    motionSettings = hashMapOf<String, Any>().also { // НЕ ПИХАТЬ ДОБАВЛЕНИЕ СРАЗУ В ( сюда )
                        it["yaw"] = readDouble()
                        it["pitch"] = readDouble()
                    },
                    content = NetUtil.readUtf8(this@registerChannel),
                    x = readDouble(),
                    y = readDouble(),
                    z = readDouble(),
                    height = readInt(),
                    weight = readInt(),
                    texture = NetUtil.readUtf8(this@registerChannel),
                    red = readInt(),
                    green = readInt(),
                    blue = readInt(),
                    opacity = readDouble()
                )

                if (banner.motionType == MotionType.STEP_BY_TARGET) {
                    banner.motionSettings["target"] = readInt()
                    banner.motionSettings["offsetX"] = readDouble()
                    banner.motionSettings["offsetY"] = readDouble()
                    banner.motionSettings["offsetZ"] = readDouble()
                }

                val context = Context3D(V3(banner.x, banner.y, banner.z))
                banners[uuid] = banner to context

                context.addChild(rectangle {
                    align = TOP
                    origin = TOP

                    if (banner.texture.isNotEmpty()) {
                        val parts = banner.texture.split(":")
                        textureLocation = UIEngine.clientApi.resourceManager().getLocation(parts[0], parts[1])
                    }
                    if (banner.content.isNotEmpty()) {
                        text(banner.content, banner, this)
                    }

                    size = V3(banner.weight.toDouble(), banner.height.toDouble())
                    color = Color(banner.red, banner.green, banner.blue, banner.opacity)
                    context.renderDistance = 75.0
                    context.rotation =
                        Rotation(Math.toRadians(banner.motionSettings["yaw"].toString().toDouble()), 0.0, 1.0, 0.0)
                    rotation =
                        Rotation(Math.toRadians(banner.motionSettings["pitch"].toString().toDouble()), 1.0, 0.0, 0.0)

                    if (banner.motionSettings["xray"].toString().toBoolean()) {
                        beforeRender = {
                            GlStateManager.disableDepth()
                        }
                        afterRender = {
                            GlStateManager.enableDepth()
                        }
                    }
                })
                UIEngine.worldContexts.add(context)
            }
        }

        mod.registerChannel("banner:change-content") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            banners[uuid]?.let { pair ->
                if (pair.second.children.isNotEmpty()) {
                    val element = (pair.second.children[0] as RectangleElement)
                    element.children.clear()
                    text(NetUtil.readUtf8(this), pair.first, element)
                }
            }
        }

        mod.registerChannel("banner:size-text") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            banners[uuid]?.let { pair ->
                repeat(readInt()) {
                    val line = readInt()
                    val newScale = readDouble()

                    sizes[uuid to line] = newScale
                    val element = pair.second.children[0] as RectangleElement

                    element.children[line * 2].animate(0.2) {
                        scale = V3(newScale, newScale, newScale)
                        offset.y = -(-3 - line * 12) * newScale
                    }
                    element.children[line * 2 + 1].animate(0.2) {
                        scale = V3(newScale, newScale, newScale)
                        offset.y = -(-3 - line * 12 - 0.75) * newScale
                    }
                }
            }
        }

        mod.registerChannel("banner:remove") {
            repeat(readInt()) {
                val uuid = UUID.fromString(NetUtil.readUtf8(this))
                banners[uuid]?.let {
                    UIEngine.worldContexts.remove(it.second)
                    banners.remove(uuid)
                }
            }
        }

        mod.registerHandler<NameTemplateRender> {
            if (entity !is Entity)
                return@registerHandler
            val current = entity as Entity
            banners.filter { it.value.first.motionType == MotionType.STEP_BY_TARGET }.forEach { (_, pair) ->
                pair.first.motionSettings["target"]?.let {
                    if (it.toString().toInt() == current.entityId) {
                        pair.second.animate(0.01) {
                            offset.x = current.x + pair.first.motionSettings["offsetX"].toString().toDouble()
                            offset.y = current.y + pair.first.motionSettings["offsetY"].toString().toDouble()
                            offset.z = current.z + pair.first.motionSettings["offsetZ"].toString().toDouble()
                        }
                    }
                }
            }
        }

        mod.registerHandler<RenderTickPre> {
            val player = UIEngine.clientApi.minecraft().player
            val timer = UIEngine.clientApi.minecraft().timer
            val yaw =
                (player.rotationYaw - player.prevRotationYaw) * timer.renderPartialTicks + player.prevRotationYaw
            val pitch =
                (player.rotationPitch - player.prevRotationPitch) * timer.renderPartialTicks + player.prevRotationPitch

            banners.filter { it.value.first.watchingOnPlayer }.forEach {
                it.value.second.rotation = Rotation(-yaw * Math.PI / 180 + Math.PI, 0.0, 1.0, 0.0)
                it.value.second.children[0].rotation = Rotation(-pitch * Math.PI / 180, 1.0, 0.0, 0.0)
            }
        }
    }

    fun text(text: String, banner: Banner, rectangle: RectangleElement) {
        text.split("\n").forEachIndexed { index, line ->
            val currentSize = sizes[banner.uuid to index] ?: 1.0
            val v3 = V3(currentSize, currentSize, currentSize)

            rectangle + text {
                align = TOP
                origin = TOP
                content = line
                size = V3(banner.weight.toDouble(), banner.height.toDouble())
                color = WHITE
                offset.z = -0.05
                offset.y = -(-3 - index * 12) * currentSize
                scale = v3
            }
            rectangle + text {
                align = TOP
                origin = TOP
                content = toBlackText(line)
                size = V3(banner.weight.toDouble(), banner.height.toDouble())
                color = Color(0, 0, 0, 0.82)
                offset.z = -0.002
                offset.y = -(-3 - index * 12 - 0.75) * currentSize
                offset.x += 0.75 * currentSize
                scale = v3
            }
        }
    }
}
