package standard.world.banners

import asColor
import dev.xdark.clientapi.entity.Entity
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.NameTemplateRender
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.math.Vec3d
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import me.func.protocol.data.element.Banner
import me.func.protocol.data.element.MotionType
import me.func.protocol.math.RadiusCheck
import org.lwjgl.input.Mouse
import readRgb
import readV3
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.UIEngine.clientApi
import ru.cristalix.uiengine.element.*
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import writeUuid
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

object BannersManager {

    private val banners = hashMapOf<UUID, Triple<Banner, Context3D, CarvedRectangle>>()
    private val sizes = hashMapOf<Pair<UUID, Int>, Double>()

    private fun toBlackText(string: String) =
        "¨222200" + string.replace(Regex("(§[0-9a-fA-F]|¨......)"), "¨222200")

    fun get(uuid: UUID) = banners[uuid]

    init {
        var pressed = false

        mod.registerHandler<GameLoop> {

            val pressedLeft = Mouse.isButtonDown(MouseButton.LEFT.ordinal)
            val pressedRight = Mouse.isButtonDown(MouseButton.RIGHT.ordinal)

            if (pressed && !pressedLeft && !pressedRight) pressed = false
            else if (!pressed && (pressedLeft || pressedRight)) {

                pressed = true

                val player = clientApi.minecraft().player

                banners.filterValues {
                    val origin = it.second.offset
                    RadiusCheck.inRadius(
                        6.0,
                        doubleArrayOf(origin.x, origin.y, origin.z),
                        doubleArrayOf(player.x, player.y, player.z)
                    )
                }.filterValues {

                    val origin = it.second.offset

                    val vector = Vec3d.of(
                        origin.x - player.x,
                        origin.y - player.y - 1.5 - it.first.height / 16.0 / 2.0,
                        origin.z - player.z
                    ).normalize()

                    player.lookVec.dotProduct(vector.x, vector.y, vector.z) > 0.9
                }.forEach { (uuid, _) ->

                    clientApi.clientConnection().sendPayload(
                        BannersController.CLICK_BANNER_CHANNEL, Unpooled.buffer().writeUuid(uuid)
                            .writeInt(if (pressedLeft) MouseButton.LEFT.ordinal else MouseButton.RIGHT.ordinal)
                    )
                }
            }
        }

        mod.registerHandler<NameTemplateRender> {

            if (entity !is Entity) return@registerHandler

            val current = entity as Entity

            banners.filter { it.value.first.motionType == MotionType.STEP_BY_TARGET }.forEach { (_, triple) ->

                triple.first.motionSettings["target"]?.let {

                    if (it.toString().toInt() == current.entityId) {

                        triple.second.animate(0.01) {
                            offset.x = current.x + triple.first.motionSettings["offsetX"].toString().toDouble()
                            offset.y = current.y + triple.first.motionSettings["offsetY"].toString().toDouble()
                            offset.z = current.z + triple.first.motionSettings["offsetZ"].toString().toDouble()
                        }
                    }
                }
            }
        }

        mod.registerHandler<RenderTickPre> {

            val player = clientApi.minecraft().player
            val timer = clientApi.minecraft().timer
            val yaw =
                (player.rotationYaw - player.prevRotationYaw) * timer.renderPartialTicks + player.prevRotationYaw
            val pitch =
                (player.rotationPitch - player.prevRotationPitch) * timer.renderPartialTicks + player.prevRotationPitch

            banners.forEach {

                val context = it.value.second
                val banner = it.value.first

                val size = sqrt(abs(banner.weight * banner.height / 100.0 / 100.0))

                if (banner.watchingOnPlayer) {
                    context.rotation = Rotation(-yaw * Math.PI / 180 + Math.PI, 0.0, 1.0, 0.0)
                    context.children[0].rotation = Rotation(-pitch * Math.PI / 180, 1.0, 0.0, 0.0)
                }

                if (banner.watchingOnPlayerWithoutPitch) {
                    context.rotation = Rotation(-yaw * Math.PI / 180 + Math.PI, 0.0, 1.0, 0.0)
                }

                context.enabled = RadiusCheck.inRadius(
                    75.0 * sqrt(size),
                    doubleArrayOf(context.offset.x, context.offset.y, context.offset.z),
                    doubleArrayOf(player.x, player.y, player.z)
                )
            }
        }
    }

    fun getTextureLocation(texture: String): ResourceLocation? {
        val parts = texture.split(":")
        return if (texture.isNotEmpty()) ResourceLocation.of(parts[0], parts[1]) else null
    }

    fun remove(size: Int, buf: ByteBuf) = repeat(size) { remove(buf.readId()) }

    private fun remove(uuid: UUID) = banners[uuid]?.let {
        UIEngine.worldContexts.remove(it.second)
        banners.remove(uuid)
    }

    fun textSize(buf: ByteBuf) {

        val uuid = buf.readId()

        banners[uuid]?.let { triple ->
            repeat(buf.readInt()) {

                val line = buf.readInt()
                val newScale = buf.readDouble()

                sizes[uuid to line] = newScale

                val text = triple.third.children.filterIsInstance<TextElement>().toTypedArray()

                text[line * 2].animate(0.2) {
                    scale = V3(newScale, newScale, newScale)
                    offset.y = -(-3 - line * 12) * newScale
                }
                text[line * 2 + 1].animate(0.2) {
                    scale = V3(newScale, newScale, newScale)
                    offset.y = -(-3 - line * 12 - 0.75) * newScale
                }
            }
        }
    }

    fun text(text: String, banner: Banner, element: Parent) {

        text.split("\n").forEachIndexed { index, line ->

            val currentSize = sizes[banner.uuid to index] ?: 1.0
            val v3 = V3(currentSize, currentSize, currentSize)

            element + text {
                align = TOP
                origin = TOP

                content = line

                size = V3(banner.weight.toDouble(), banner.height.toDouble())
                scale = v3

                color = WHITE

                offset.z = -0.05
                offset.y = -(-3 - index * 12) * currentSize
            }

            element + text {
                align = TOP
                origin = TOP

                content = toBlackText(line)

                size = V3(banner.weight.toDouble(), banner.height.toDouble())
                scale = v3

                color = Color(0, 0, 0, 0.82)

                offset.z = -0.002
                offset.y = -(-3 - index * 12 - 0.75) * currentSize
                offset.x += 0.75 * currentSize
            }
        }
    }

    fun new(size: Int, byteBuf: ByteBuf) = repeat(size) { new(byteBuf) }

    private fun new(buf: ByteBuf) {

        val uuid = buf.readId()
        val banner = Banner()

        banner.uuid = uuid
        banner.motionType = MotionType.values()[buf.readInt()]
        banner.watchingOnPlayer = buf.readBoolean()
        banner.watchingOnPlayerWithoutPitch = buf.readBoolean()
        banner.motionSettings = hashMapOf<String, Any>().also { // НЕ ПИХАТЬ ДОБАВЛЕНИЕ СРАЗУ В ( сюда )
            it["yaw"] = buf.readDouble()
            it["pitch"] = buf.readDouble()
            it["xray"] = buf.readBoolean()
        }

        banner.content = NetUtil.readUtf8(buf)
        val v3 = buf.readV3()
        banner.x = v3.x
        banner.y = v3.y
        banner.z = v3.z
        banner.height = buf.readInt()
        banner.weight = buf.readInt()
        banner.texture = NetUtil.readUtf8(buf)
        banner.color = buf.readRgb()
        banner.opacity = buf.readDouble()
        banner.carveSize = buf.readDouble()

        if (banner.motionType == MotionType.STEP_BY_TARGET) {
            banner.apply {
                motionSettings["target"] = buf.readInt()
                motionSettings["offsetX"] = buf.readDouble()
                motionSettings["offsetY"] = buf.readDouble()
                motionSettings["offsetZ"] = buf.readDouble()
            }
        }

        val triple = getTriple(banner)

        banners[uuid] = triple

        triple.second.addChild(triple.third)
        UIEngine.worldContexts.add(triple.second)
    }

    private fun getTriple(banner: Banner): Triple<Banner, Context3D, CarvedRectangle> {

        val context = Context3D(
            V3(
                banner.x,
                banner.y,
                banner.z
            )
        )

        val carved = carved {
            carveSize = banner.carveSize

            align = TOP
            origin = TOP

            this.textureLocation = getTextureLocation(banner.texture)

            val sized = V3(banner.weight.toDouble(), banner.height.toDouble())

            if (banner.content.isNotEmpty()) {
                text(banner.content, banner, this)
            }

            size = sized

            color = banner.color.asColor()
            color.alpha = banner.opacity

            context.rotation =
                Rotation(Math.toRadians(banner.motionSettings["yaw"].toString().toDouble()), 0.0, 1.0, 0.0)
            rotation =
                Rotation(Math.toRadians(banner.motionSettings["pitch"].toString().toDouble()), 1.0, 0.0, 0.0)

            if (banner.motionSettings["xray"].toString().toBoolean()) {
                beforeRender {
                    GlStateManager.disableDepth()
                }
                afterRender {
                    GlStateManager.enableDepth()
                }
            }
        }

        return Triple(banner, context, carved)
    }
}