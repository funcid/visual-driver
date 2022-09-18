package standard.alert

import lazyText
import me.func.protocol.data.status.MessageStatus
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*

class SystemMessageAlert {
    private val offsetX = 10.0
    private val buttonY = 5.0
    private var buttonX = 70.0
    private val symbol: TextElement by lazyText {
        content = "Загрузка..."
        scale = V3(2.0, 2.0)
        align = LEFT
        origin = LEFT
        offset.x += offsetX
    }
    private val description: TextElement by lazyText {
        content = "Загрузка"
        shadow = true
        origin = LEFT
        align = LEFT
        offset.x += 30
    }

    private val message = UIEngine.overlayContext + carved {
        carveSize = 2.0
        align = BOTTOM
        origin = BOTTOM
        offset.y -= 40.0
        size = V3(buttonX, buttonY + 2 * offsetX)
        color = Color(203, 65, 84, 0.60)

        +symbol
        +description
        enabled = false
    }

    init {
        mod.registerChannel("anime:message") {
            when (MessageStatus.values()[readInt()]) {
                MessageStatus.FINE -> {
                    symbol.content = "!"
                    message.color = Color(74, 140, 236, 0.60)
                }

                MessageStatus.WARN -> {
                    symbol.content = "?"
                    message.color = Color(255, 157, 66, 0.60)
                }

                MessageStatus.ERROR -> {
                    symbol.content = "X"
                    message.color = Color(203, 65, 84, 0.60)
                }
            }

            val duration = readDouble()
            val text = readUtf8()
            val textLines = text.split("\n")
            val maxWidth = textLines.maxOf(UIEngine.clientApi.fontRenderer()::getStringWidth)
            message.size.x = maxWidth + buttonX
            description.content = text
            message.enabled = true

            UIEngine.schedule(duration) {
                message.enabled = false
            }
        }
    }
}