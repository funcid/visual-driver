package standard

import dev.xdark.feder.NetUtil
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

object Title {

    private lateinit var element: TextElement

    private val message = rectangle {
        origin = CENTER
        align = CENTER
        size = V3(400.0, 250.0, 0.0)
        element = +text {
            origin = CENTER
            align = CENTER
            size = V3(400.0, 100.0, 0.0)
            color = Color(0, 0, 0, 0.2)
            shadow = true
        }
        enabled = false
    }

    init {
        UIEngine.overlayContext + message

        Standard.mod.registerChannel("func:title") {
            element.content = NetUtil.readUtf8(this)
            message.enabled = true
            element.animate(0.3) {
                color.red = 255
                color.green = 140
                color.blue = 185
                color.alpha = 1.0
                scale.x = 2.2
                scale.y = 2.2
            }
            UIEngine.schedule(3.1) {
                element.animate(3.15) {
                    scale.x = 20.0
                    scale.y = 20.0
                    color.alpha = 0.0
                }
            }
            UIEngine.schedule(3.3) {
                message.enabled = false
                element.color.red = 0
                element.color.green = 0
                element.color.blue = 0
                element.color.alpha = 0.0
                element.scale.x = 1.0
                element.scale.y = 1.0
            }
        }
    }
}