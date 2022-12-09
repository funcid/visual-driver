package standard.storage.menu

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

class Reconnect {
    companion object {

        private val offsetX = 10.0
        private val buttonY = 20.0
        private val offsetY = 65.0
        private var originalString = ""
        private var buttonX = 50.0
        private var secondsLeft = -1

        init {
            var element: TextElement?
            var buttonText: TextElement?
            var button: CarvedRectangle?
            val container = UIEngine.overlayContext + carved {
                enabled = false
                color = Color(20, 98, 41, 0.62)
                size = V3(200.0, buttonY + 2 * offsetX)
                align = BOTTOM
                origin = BOTTOM
                offset.y += offsetY / 2
                carveSize = 2.0
                element = +text {
                    align = LEFT
                    origin = LEFT
                    shadow = true
                    offset.x += offsetX
                }
                button = +carved {
                    align = RIGHT
                    origin = RIGHT
                    offset.x -= offsetX
                    size = V3(buttonX, buttonY)
                    color = Color(34, 174, 73, 1.0)
                    buttonText = +text {
                        align = CENTER
                        origin = CENTER
                        shadow = true
                    }
                    onClick {
                        clientApi.clientConnection().sendPayload("func:reconnect", Unpooled.EMPTY_BUFFER)
                        secondsLeft = 0
                    }
                    val normalColor = Color(34, 174, 73, 1.0)
                    val hoveredColor = Color(73, 223, 115, 1.0)
                    color = normalColor
                    onHover {
                        animate(0.08, Easings.QUINT_OUT) {
                            color = if (hovered) hoveredColor else normalColor
                        }
                    }
                }
            }

            mod.registerChannel("func:reconnect") {
                secondsLeft = readInt()
                originalString = NetUtil.readUtf8(this).replace("&", "§")
                buttonText?.content = NetUtil.readUtf8(this).replace("&", "§")
                button?.size?.x = clientApi.fontRenderer().getStringWidth(buttonText?.content) + offsetX
                container.size.x = (button?.size?.x ?: buttonX) + 3 * offsetX + clientApi.fontRenderer()
                    .getStringWidth(originalString + "00000")
            }

            var lastSecond = 0L
            mod.registerHandler<GameLoop> {
                val now = System.currentTimeMillis()
                if (now - lastSecond > 1_000 && element != null) {
                    if (secondsLeft < 1 && container.enabled) {
                        container.animate(0.7, Easings.QUINT_BOTH) { container.offset.y += offsetY * 1.5 }
                        UIEngine.schedule(0.7) { container.enabled = false }
                    } else if (secondsLeft > 0 && !container.enabled) {
                        container.enabled = true
                        container.animate(0.5, Easings.QUINT_BOTH) { container.offset.y -= offsetY * 1.5 }
                    }

                    lastSecond = now
                    secondsLeft--
                    if (secondsLeft < 0) return@registerHandler
                    element!!.content = "$originalString ${(secondsLeft / 60).toString().padStart(2, '0')}:${
                        (secondsLeft % 60).toString().padStart(2, '0')
                    }"
                }
            }
        }
    }
}
