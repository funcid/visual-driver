import dev.xdark.clientapi.entity.EntityLiving
import dev.xdark.clientapi.event.render.NameTemplateRender
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.feder.NetUtil
import me.func.protocol.element.Banner
import me.func.protocol.element.MotionType
import ru.cristalix.clientapi.mod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import sun.security.jgss.GSSToken.readInt
import java.util.*

object Banners {

    private val banners = mutableMapOf<UUID, Pair<Banner, Context3D>>()
    private val sizes = mutableMapOf<Pair<UUID, Int>, Double>()

    init {
        fun text(text: String, banner: Banner, rectangle: RectangleElement) {
            text.split("\n").forEachIndexed { index, line ->
                rectangle +text {
                    align = TOP
                    origin = TOP
                    content = line
                    size = V3(banner.weight.toDouble(), banner.height.toDouble())
                    color = WHITE
                    offset.z -= 0.01
                    offset.y -= -10 - index * 12

                    shadow = true

                    sizes[banner.uuid to index]?.let { scale = V3(it, it, it) }
                }
            }
        }

        App::class.mod.registerChannel("banner:new") {
            repeat(readInt()) {
                val uuid = UUID.fromString(NetUtil.readUtf8(this))
                val banner = Banner(
                    uuid,
                    MotionType.values()[readInt()],
                    readBoolean(),
                    mutableMapOf(
                        "yaw" to readDouble(),
                        "pitch" to readDouble(),
                    ), NetUtil.readUtf8(this@registerChannel),
                    readDouble(),
                    readDouble(),
                    readDouble(),
                    readInt(),
                    readInt(),
                    NetUtil.readUtf8(this@registerChannel),
                    readInt(),
                    readInt(),
                    readInt(),
                    readDouble()
                )
                val context = Context3D(V3(banner.x, banner.y, banner.z))

                context.addChild(rectangle {
                    if (banner.texture.isNotEmpty()) {
                        val parts = banner.texture.split(":")
                        textureLocation = UIEngine.clientApi.resourceManager().getLocation(parts[0], parts[1])
                    }
                    if (banner.content.isNotEmpty()) {
                        text(banner.content, banner, this)
                    }

                    size = V3(banner.weight.toDouble(), banner.height.toDouble())
                    color = Color(banner.red, banner.green, banner.blue, banner.opacity)
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
                banners[uuid] = banner to context
            }
        }

        App::class.mod.registerChannel("banner:change-content") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            banners[uuid]?.let { pair ->
                val element = (pair.second.children[0] as RectangleElement)
                element.children.clear()

                text(NetUtil.readUtf8(this), pair.first, element)
            }
        }

        App::class.mod.registerChannel("banner:size-text") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            banners[uuid]?.let { pair ->
                repeat(readInt()) {
                    val line = readInt()
                    val scale = readDouble()

                    sizes[uuid to line] = scale
                    (pair.second.children[0] as RectangleElement).children[line].scale = V3(scale, scale, scale)
                }
            }
        }

        App::class.mod.registerChannel("banner:remove") {
            repeat(readInt()) {
                val uuid = UUID.fromString(NetUtil.readUtf8(this))
                banners[uuid]?.let {
                    UIEngine.worldContexts.remove(it.second)
                    banners.remove(uuid)
                }
            }
        }

        registerHandler<NameTemplateRender> {
            if (entity !is EntityLiving)
                return@registerHandler
            val current = entity as EntityLiving
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

        registerHandler<RenderTickPre> {
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
}