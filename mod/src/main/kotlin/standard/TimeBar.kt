package standard

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.Easings
import ru.cristalix.uiengine.utility.LEFT
import ru.cristalix.uiengine.utility.Property
import ru.cristalix.uiengine.utility.Rotation
import ru.cristalix.uiengine.utility.TOP
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.WHITE
import ru.cristalix.uiengine.utility.get
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.set
import ru.cristalix.uiengine.utility.text
import sun.security.jgss.GSSToken.readInt

context(KotlinMod)
class TimeBar {

    private lateinit var line: RectangleElement
    private lateinit var content: TextElement

    init {
        val cooldown = rectangle {
            offset.y += 30
            origin = TOP
            align = TOP
            size = V3(180.0, 5.0, 0.0)
            color = Color(0, 0, 0, 0.62)
            line = +rectangle {
                origin = LEFT
                align = LEFT
                size = V3(180.0, 5.0, 0.0)
                color = Color(42, 102, 189, 1.0)
            }
            content = +text {
                origin = TOP
                align = TOP
                color = WHITE
                shadow = true
                content = "Загрузка..."
                offset.y -= 15
            }
            enabled = false
        }

        var time = 0
        var currentTime = System.currentTimeMillis()

        registerHandler<GameLoop> {
            if (System.currentTimeMillis() - currentTime > 1000) {
                time--
                currentTime = System.currentTimeMillis()
                content.content = content.content.dropLast(7) + (time / 60).toString()
                    .padStart(2, '0') + ":" + (time % 60).toString().padStart(2, '0') + " ⏳"
            }
        }

        registerChannel("func:bar") {
            val text = NetUtil.readUtf8(this) + " XX:XX ⏳"
            time = this.readInt()

            line.color = Color(readInt(), readInt(), readInt(), 1.0)

            if (time == 0) {
                line.animate(1.0) { size.x = 180.0 }
                cooldown.enabled = false
                return@registerChannel
            }

            cooldown.enabled = true
            content.content = text
            line.animate(time - 0.1) {
                size.x = 0.0
            }
            UIEngine.schedule(time) {
                cooldown.enabled = false
                line.size.x = 180.0
            }
        }

        fun dropNumber(text: String, size: Double, light: Color) {
            val item = text {
                align = CENTER
                origin = CENTER
                properties[Property.ParentSizeX] *= 0.8
                color = light
                shadow = true
                content = text
            }

            UIEngine.overlayContext + item

            item.animate(0.45, Easings.BACK_BOTH) {
                scale.x *= size
                scale.y *= size
            }
            UIEngine.schedule(0.45) {
                item.animate(0.15) {
                    rotation = Rotation(Math.PI / 4, 0.0, 0.0, 1.0)
                    color.alpha = 0.1
                }
            }
            UIEngine.schedule(0.65) {
                UIEngine.overlayContext.removeChild(item)
            }
        }

        registerChannel("func:attention") {
            val secondsTotal = 3

            dropNumber(3.toString(), 5.0, Color(255, 255, 85))
            UIEngine.schedule((secondsTotal - 1.0) / 3) { dropNumber(2.toString(), 5.2, Color(255, 85, 85)) }
            UIEngine.schedule((secondsTotal - 1.0) / 3 * 2) { dropNumber(1.toString(), 5.5, Color(170, 10, 10)) }
            UIEngine.schedule((secondsTotal - 1.0) / 3 * 3) { dropNumber("§lGO!", 2.2, Color(100, 255, 100)) }
        }

        UIEngine.overlayContext + cooldown
    }
}