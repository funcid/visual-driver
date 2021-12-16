import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

object ScreenAlert {

    private var topmessage: RectangleElement? = null

    init {

        App::class.mod.registerChannel("func:top-alert") {
            val resolution = UIEngine.clientApi.resolution()

            if (topmessage == null) {
                topmessage = rectangle {
                    size = V3(resolution.scaledWidth_double, resolution.scaledHeight_double)
                    align = Relative.CENTER
                    origin = Relative.CENTER
                    addChild(rectangle {
                        size = V3(resolution.scaledWidth_double, 0.0)
                        align = Relative.TOP
                        origin = Relative.TOP
                        color = Color(0, 0, 0, 0.6)
                        offset = V3(0.0, -20.0)
                        addChild(text {
                            align = V3(0.5, 0.75)
                            origin = Relative.CENTER
                            scale = V3(1.3, 1.3)
                            enabled = false
                        })
                    })
                }
                UIEngine.overlayContext + topmessage!!
            }

            topmessage!!.size = V3(resolution.scaledWidth_double, resolution.scaledHeight_double)

            val localBox = topmessage!!.children[0] as RectangleElement
            val message = localBox.children[0] as TextElement

            localBox.size = V3(resolution.scaledWidth_double, localBox.size.y)
            message.content = NetUtil.readUtf8(this)
            message.enabled = true

            localBox.animate(5, Easings.BACK_OUT) {
                size.y = 50.0
            }
            UIEngine.schedule(7) {
                localBox.animate(5, Easings.BACK_OUT) {
                    size.y = 0.0
                }
            }
        }
    }
}