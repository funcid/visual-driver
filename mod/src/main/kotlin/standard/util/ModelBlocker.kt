package standard.util

import dev.xdark.clientapi.event.gui.ScreenDisplay
import lazyText
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.Easings
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.WHITE
import ru.cristalix.uiengine.utility.text

class ModelBlocker {

    private val locker: TextElement by lazyText {
        color = WHITE
        shadow = true
        scale = V3(3.0, 3.0, 3.0)
        align = CENTER
        origin = CENTER
        content = "Недоступно на\nданном режиме"
        enabled = false
        UIEngine.overlayContext + locker
    }

    init {
        var disabled = false

        mod.registerHandler<ScreenDisplay> {
            if (!disabled) return@registerHandler

            if (screen::class.java.simpleName == "aqW") {
                isCancelled = true

                locker.enabled = true
                UIEngine.schedule(1.5) {
                    animate(0.5, Easings.BACK_BOTH) {
                        locker.scale.x = 0.6
                        locker.scale.y = 0.6
                    }
                }
                UIEngine.schedule(2.1) {
                    locker.enabled = false
                    locker.scale.x = 3.0
                    locker.scale.y = 3.0
                }
            }
        }

        mod.registerChannel("func:break-ui") {
            disabled = readBoolean()
        }
    }
}
