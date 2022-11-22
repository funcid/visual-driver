package standard.ui

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.gui.ingame.ChatScreen
import dev.xdark.feder.NetUtil
import lazyText
import me.func.protocol.math.Position
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*

class OverlayText {

    private val bottomRight: TextElement by lazyText {
        origin = BOTTOM_RIGHT
        align = BOTTOM_RIGHT
        shadow = true
        offset.y -= 15
        offset.x -= 3
        UIEngine.overlayContext + this
    }

    private val bottomLeft: TextElement by lazyText {
        origin = BOTTOM_LEFT
        align = BOTTOM_LEFT
        shadow = true
        offset.y -= 5
        offset.x += 3
        UIEngine.overlayContext + this
    }

    private val topRight: TextElement by lazyText {
        origin = TOP_RIGHT
        align = TOP_RIGHT
        shadow = true
        offset.y += 2
        offset.x -= 3
        UIEngine.overlayContext + this
    }

    private val topLeft: TextElement by lazyText {
        origin = TOP_LEFT
        align = TOP_LEFT
        shadow = true
        offset.y += 2
        offset.x += 3
        UIEngine.overlayContext + this
    }

    private val top: TextElement by lazyText {
        origin = TOP
        align = TOP
        shadow = true
        offset.y += 2
        UIEngine.overlayContext + this
    }

    private val centerLeft: TextElement by lazyText {
        origin = LEFT
        align = LEFT
        shadow = true
        offset.x += 3
        UIEngine.overlayContext + this
    }
    private val centerRight: TextElement by lazyText {
        origin = RIGHT
        align = RIGHT
        shadow = true
        offset.x -= 3
        UIEngine.overlayContext + this
    }

    init {
        mod.registerChannel("anime:overlay") {
            when (Position.values()[readInt()]) {
                Position.BOTTOM_RIGHT -> bottomRight.content = NetUtil.readUtf8(this)
                Position.BOTTOM_LEFT -> bottomLeft.content = NetUtil.readUtf8(this)
                Position.TOP_RIGHT -> topRight.content = NetUtil.readUtf8(this)
                Position.TOP_LEFT -> topLeft.content = NetUtil.readUtf8(this)
                Position.RIGHT -> centerRight.content = NetUtil.readUtf8(this)
                Position.LEFT -> centerLeft.content = NetUtil.readUtf8(this)
                Position.TOP -> top.content = NetUtil.readUtf8(this)
                else -> {}
            }
            registerHandler<GameLoop> {
                bottomLeft.enabled = UIEngine.clientApi.minecraft().currentScreen() !is ChatScreen
            }
        }
    }
}