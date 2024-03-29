package standard.alert

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.Easings
import ru.cristalix.uiengine.utility.Relative
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.text

class CursorAlert {
    private var hints = ArrayList<Pair<Long, AbstractElement>>()

    init {
        val timeLife = 3 * 1000

        mod.registerChannel("func:cursor") {
            val hint = rectangle {
                offset = Relative.CENTER
                align = Relative.CENTER
                scale = V3(1.0, 1.0, 1.0)
                +text {
                    offset.x -= 20
                    shadow = true
                    scale = V3(1.0, 1.0, 1.0)
                    content = NetUtil.readUtf8(this@registerChannel)
                    color = Color(255, 255, 255, 1.0)
                }
            }
            UIEngine.overlayContext + hint

            hint.animate(timeLife / 1000, Easings.SINE_BOTH) {
                offset.x += 70 * (Math.random() - 0.5)
                offset.y += 70 * Math.random()
            }

            hints.add(System.currentTimeMillis() to hint)
        }

        mod.registerHandler<GameLoop> {
            if (hints.isEmpty()) return@registerHandler

            val time = System.currentTimeMillis()

            hints.removeIf {
                val remove = time - it.first > timeLife

                if (remove) UIEngine.overlayContext.removeChild(it.second)

                return@removeIf remove
            }
        }
    }
}
