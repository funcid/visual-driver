package standard

import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*

class OverlayText {

    private val bottomRight = text {
        origin = BOTTOM_RIGHT
        align = BOTTOM_RIGHT
        shadow = true
        offset.y -= 15
        offset.x -= 3
    }

    private val bottomLeft = text {
        origin = BOTTOM_LEFT
        align = BOTTOM_LEFT
        shadow = true
        offset.y -= 5
        offset.x += 3
    }

    private val topRight = text {
        origin = TOP_RIGHT
        align = TOP_RIGHT
        shadow = true
        offset.y += 2
        offset.x -= 3
    }

    private val topLeft = text {
        origin = TOP_LEFT
        align = TOP_LEFT
        shadow = true
        offset.y += 2
        offset.x += 3
    }

    init {
        UIEngine.overlayContext.addChild(bottomRight, bottomLeft, topRight, topLeft)

        mod.registerChannel("anime:bottomright") {
            bottomRight.content = NetUtil.readUtf8(this)
        }
        mod.registerChannel("anime:bottomleft") {
            bottomLeft.content = NetUtil.readUtf8(this)
        }
        mod.registerChannel("anime:topright") {
            topRight.content = NetUtil.readUtf8(this)
        }
        mod.registerChannel("anime:topleft") {
            topLeft.content = NetUtil.readUtf8(this)
        }
    }
}