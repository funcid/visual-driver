package experimental.storage.menu

import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import ru.cristalix.uiengine.UIEngine.clientApi
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.onMouseUp
import ru.cristalix.uiengine.utility.*
import java.util.*

class Confirmation(var uuid: UUID, lines: String) : ContextGui() {

    private val padding = 8.0
    private val buttonWidth = 80.0
    private val buttonHeight = 20.0

    lateinit var message: TextElement
    lateinit var agree: CarvedRectangle
    lateinit var disagree: CarvedRectangle

    private fun answer(answer: Boolean) {
        clientApi.clientConnection().sendPayload("func:yesno", Unpooled.buffer().apply {
            NetUtil.writeUtf8(this, uuid.toString())
            writeBoolean(answer)
        })
        close()
    }

    val container = +carved {
        color = Color(42, 102, 189, 0.28)
        align = CENTER
        origin = CENTER
        carveSize = 2.0
        message = +text {
            align = TOP
            origin = TOP
            content = lines.replace("&", "§")
            color = WHITE
            offset.y += padding
            shadow = true
        }
        size = V3(
            buttonWidth * 2 + 5 * padding / 2,
            message.lineHeight * message.content.split("\n").size + buttonHeight + padding * 3
        )

        fun button(offsetX: Double, title: String, normal: Color, hover: Color, answer: Boolean) = carved {
            align = BOTTOM
            origin = BOTTOM
            size = V3(buttonWidth, buttonHeight)
            offset.x += offsetX
            offset.y -= padding
            carveSize = 2.0
            color = normal
            var was = false
            onHover {
                if (was == hovered)
                    return@onHover
                was = hovered
                animate(0.1, Easings.CUBIC_OUT) {
                    color = if (hovered) hover else normal
                }
            }
            +text {
                align = CENTER
                origin = CENTER
                content = title
                shadow = true
            }
            onMouseUp { answer(answer) }
        }

        agree = +button(
            -(buttonWidth / 2 + padding / 4),
            "Подтвердить",
            Color(34, 174, 73, 1.0),
            Color(73, 223, 115, 1.0),
            true
        )
        disagree = +button(
            buttonWidth / 2 + padding / 4,
            "Закрыть",
            Color(160, 29, 40, 1.0),
            Color(231, 61, 75, 1.0),
            false
        )
    }

    init {
        color = Color(0, 0, 0, 0.83)
    }
}
