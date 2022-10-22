package standard.alert

import lazyText
import me.func.protocol.data.status.MessageStatus
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.Task
import ru.cristalix.uiengine.utility.*

class SystemMessageAlert {
    private var task: Task? = null

    private val symbol: TextElement by lazyText {
        content = "Загрузка..."
        scale = V3(2.0, 2.0)
        align = LEFT
        origin = LEFT
        offset.x += 10.0
    }
    private val description: TextElement by lazyText {
        content = "Загрузка"
        origin = LEFT
        align = LEFT
        offset.x += 25.0
    }

    private val container = UIEngine.postOverlayContext + carved {
        carveSize = 2.0
        align = BOTTOM
        origin = BOTTOM
        offset.y -= 40.0
        color = Color(203, 65, 84, 0.60)
        size = V3(70.0, 25.0)

        +symbol
        +description
        enabled = false
    }

    init {
        mod.registerChannel("anime:message") {
            when (MessageStatus.values()[readInt()]) {
                MessageStatus.FINE -> {
                    symbol.content = "!"
                    container.color = Color(74, 140, 236, 0.60)
                }

                MessageStatus.WARN -> {
                    symbol.content = "?"
                    container.color = Color(255, 157, 66, 0.60)
                }

                MessageStatus.ERROR -> {
                    symbol.content = "X"
                    container.color = Color(203, 65, 84, 0.60)
                }
            }

            val duration = readDouble()
            description.content = readUtf8()
            container.size.x = UIEngine.clientApi.fontRenderer().getStringWidth(description.content) + 35.0
            container.enabled = true
            task?.cancelled = true

            task = UIEngine.schedule(duration) {
                container.enabled = false
            }
        }
    }
}