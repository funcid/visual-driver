package standard.storage

import Main.Companion.menuStack
import asColor
import io.netty.buffer.Unpooled
import me.func.protocol.data.color.GlowColor
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.onMouseUp
import ru.cristalix.uiengine.utility.*
import standard.storage.button.StorageNode
import java.util.*

interface AbstractMenu {

    var uuid: UUID

    var storage: MutableList<StorageNode<*>>

    fun open()

    fun close()

    fun closeButton(offsetY: Double, backButtonSize: Double = 19.0) = carved {
        carveSize = 2.0
        align = CENTER
        origin = CENTER
        offset.y = offsetY
        size = V3(76.0, backButtonSize)
        val normalColor = Color(160, 29, 40, 0.83)
        val hoveredColor = Color(231, 61, 75, 0.83)
        color = normalColor
        onHover {
            animate(0.08, Easings.QUINT_OUT) {
                color = if (hovered) hoveredColor else normalColor
                scale = V3(if (hovered) 1.1 else 1.0, if (hovered) 1.1 else 1.0, 1.0)
            }
        }
        onMouseUp {
            close()
            menuStack.clear()
        }
        +text {
            align = CENTER
            origin = CENTER
            color = WHITE
            scale = V3(0.9, 0.9, 0.9)
            content = "Выйти [ ESC ]"
            shadow = true
        }
    }

    fun backButton(offsetY: Double, backButtonSize: Double = 19.0) = carved {
        carveSize = 2.0
        align = CENTER
        origin = CENTER
        offset.y = offsetY
        offset.x -= 65
        size = V3(40.0, backButtonSize)
        val normalColor = GlowColor.BLUE.asColor(0.83)
        val hoveredColor = GlowColor.BLUE_LIGHT.asColor(0.83)
        color = normalColor
        onHover {
            animate(0.08, Easings.QUINT_OUT) {
                color = if (hovered) hoveredColor else normalColor
                scale = V3(if (hovered) 1.1 else 1.0, if (hovered) 1.1 else 1.0, 1.0)
            }
        }
        onMouseUp {
            menuStack.apply { pop() }.peek()?.open()
            UIEngine.clientApi.clientConnection().sendPayload("func:back", Unpooled.EMPTY_BUFFER)
        }
        +text {
            align = CENTER
            origin = CENTER
            color = WHITE
            scale = V3(0.9, 0.9, 0.9)
            content = "Назад"
            shadow = true
        }
    }
}