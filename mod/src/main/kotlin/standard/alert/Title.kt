package standard.alert

import dev.xdark.feder.NetUtil
import lazyRectangle
import lazyText
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

class Title {

    private val text: TextElement by lazyText {
        origin = CENTER
        align = CENTER
        size = V3(400.0, 100.0, 0.0)
        color = Color(0, 0, 0, 0.2)
        shadow = true
    }

    private val box: RectangleElement by lazyRectangle {
        origin = CENTER
        align = CENTER
        size = V3(400.0, 250.0, 0.0)
        +text
        enabled = false

    }

    init {
        UIEngine.overlayContext + box

        mod.registerChannel("func:title") {
            text.content = NetUtil.readUtf8(this)
            box.enabled = true
            text.animate(0.3) {
                color = WHITE
                color.alpha = 1.0
                scale.x = 2.2
                scale.y = 2.2
            }
            UIEngine.schedule(3.1) {
                text.animate(3.15) {
                    scale.x = 20.0
                    scale.y = 20.0
                    color.alpha = 0.0
                }
            }
            UIEngine.schedule(3.3) {
                box.enabled = false
                text.color = WHITE
                text.color.alpha = 0.0
                text.scale.x = 1.0
                text.scale.y = 1.0
            }
        }
    }
}
