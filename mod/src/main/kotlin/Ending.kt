import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil.readUtf8
import me.func.protocol.EndStatus
import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*

object Ending {

    private val gui = ContextGui()

    private lateinit var text: TextElement
    private lateinit var cup: RectangleElement
    private lateinit var information: RectangleElement
    private lateinit var key: TextElement
    private lateinit var value: TextElement

    private var filler: RectangleElement? = null

    private var added = false

    init {
        ExternalManager.loadPaths(
            "https://i.imgur.com/mF7DWoV.png",
            "https://i.imgur.com/vxyu2tZ.png",
            "https://i.imgur.com/zUxhQ7y.png"
        )
        if (!added || filler == null) {
            filler = rectangle {
                align = CENTER
                origin = CENTER
                size = V3(185.0, 103.0)
                color = Color(255, 255, 255, 1.0)
                offset.x -= 113
                text = +text {
                    align = CENTER
                    origin = CENTER
                    color = WHITE
                    scale = V3(2.0, 2.0)
                    offset.x += 5
                }
                cup = +rectangle {
                    align = CENTER
                    origin = CENTER
                    color = Color(255, 255, 255, 0.62)
                }
                information = +rectangle {
                    align = CENTER
                    origin = CENTER
                    size = V3(225.0, 103.0)
                    color = Color(16, 19, 19, 0.86)
                    offset.x += 205
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
            gui + filler!!
        }
        App::class.mod.registerChannel("crazy:ending") {
            val endStatus = EndStatus.values()[readInt()]
            key.content = readUtf8(this)
            value.content = readUtf8(this)
            text.content = endStatus.title
            text.offset.z = 50.0
            filler?.color = Color(endStatus.red, endStatus.green, endStatus.blue, 0.86)
            cup.textureLocation = ResourceLocation.of("cache/animation", endStatus.texture)
            cup.size = V3(63.5, 104.0)
            cup.offset.x -= endStatus.offset // картинки топ
            if (!added) {
                gui.open()
                added = true
            }
        }
    }
}