package standard.alert

import dev.xdark.clientapi.event.window.WindowResize
import dev.xdark.feder.NetUtil
import lazyRectangle
import lazyText
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

class ScreenAlert {

    private val resolution = UIEngine.clientApi.resolution()

    private val message: TextElement by lazyText {
        align = V3(0.5, 0.75)
        origin = Relative.CENTER
        scale = V3(1.3, 1.3)
        enabled = false
    }

    private val box: RectangleElement by lazyRectangle {
        size = V3(resolution.scaledWidth_double, 0.0)
        align = Relative.TOP
        origin = Relative.TOP
        color = Color(0, 0, 0, 0.6)
        offset = V3(0.0, -20.0)
        +message
    }

    private val topmessage: RectangleElement by lazyRectangle {
        size = V3(resolution.scaledWidth_double, resolution.scaledHeight_double)
        align = Relative.CENTER
        origin = Relative.CENTER
        +box
        UIEngine.overlayContext + this
    }

    init {
        mod.registerHandler<WindowResize> {
            topmessage.size = UIEngine.overlayContext.size
            box.size = V3(UIEngine.overlayContext.size.x, 0.0)
        }

        mod.registerChannel("func:top-alert") {
            val resolution = UIEngine.clientApi.resolution()

            topmessage.size = V3(resolution.scaledWidth_double, resolution.scaledHeight_double)

            box.size = V3(resolution.scaledWidth_double, box.size.y)
            message.content = NetUtil.readUtf8(this)
            message.enabled = true

            box.animate(5, Easings.BACK_OUT) { size.y = 50.0 }

            UIEngine.schedule(7) {
                box.animate(5, Easings.BACK_OUT) { size.y = 0.0 }
            }
        }
    }
}
