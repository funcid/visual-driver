
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil.readUtf8
import me.func.protocol.EndStatus
import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.eventloop.thenAnimate
import ru.cristalix.uiengine.eventloop.thenWait
import ru.cristalix.uiengine.utility.*

object Ending {

    private lateinit var text: TextElement
    private lateinit var cup: RectangleElement
    private lateinit var information: RectangleElement
    private lateinit var key: TextElement
    private lateinit var value: TextElement

    private var filler: RectangleElement? = null

    init {
        ExternalManager.loadPaths(
            "https://i.imgur.com/mF7DWoV.png",
            "https://i.imgur.com/vxyu2tZ.png",
            "https://i.imgur.com/zUxhQ7y.png"
        )
        if (filler == null) {
            filler = rectangle {
                align = CENTER
                origin = CENTER
                size = V3(185.0, 103.0)
                color = Color(255, 255, 255, 1.0)
                offset = V3(0.0, 130.0)
                text = +text {
                    align = CENTER
                    origin = CENTER
                    color = WHITE
                    scale = V3(2.0, 2.0)
                    offset = V3(5.0, 0.0, 1.0)
                }
                cup = +rectangle {
                    align = CENTER
                    origin = CENTER
                    size = V3(63.5, 104.0)
                    color = Color(255, 255, 255)
                }
            }
            information = rectangle {
                align = CENTER
                origin = CENTER
                size = V3(225.0, 103.0)
                offset = V3(20.0, 0.0, -50.0)
                color = Color(16, 19, 19, 0.86)
                key = +text {
                    align = LEFT
                    origin = LEFT
                    size = V3(20.0, 20.0)
                    color = Color(255, 255, 255, 1.0)
                    offset.x += 25
                }
                value = +text {
                    align = RIGHT
                    origin = RIGHT
                    size = V3(20.0, 20.0)
                    color = Color(255, 255, 255, 1.0)
                    offset.x -= 25
                }
            }
        }
        App::class.java.mod.registerChannel("crazy:ending") {
            val endStatus = EndStatus.values()[readInt()]
            key.content = readUtf8(this)
            value.content = readUtf8(this)

            text.content = endStatus.title
            text.offset.z = 50.0

            filler?.color = Color(endStatus.red, endStatus.green, endStatus.blue, 0.86)

            cup.offset.x -= endStatus.offset
            cup.offset.z = 50.0
            cup.textureLocation = ResourceLocation.of("cache/animation", endStatus.texture)

            UIEngine.overlayContext + filler!!
            filler!!.animate(1.0) {
                offset = V3(0.0, 0.0, 0.0)
            }.thenWait(0.1).thenAnimate(0.0) {
                filler!!.addChild(information)
            }.thenAnimate(1.5) {
                offset.x -= 113.0
                information.offset.x += 185
            }.thenWait(8.0).thenAnimate(0.7) {
                information.size.x -= 40.0
                information.offset.x -= 95
                offset.x += 55
            }.thenAnimate(0.7) {
                offset.x = 0.0
                information.offset.x = 0.0
            }.thenWait(0.1).thenAnimate(0.0) {
                filler!!.removeChild(information)
            }.thenAnimate(1.0) {
                offset.y -= 700.0
                UIEngine.overlayContext.removeChild()
            }
        }
    }
}