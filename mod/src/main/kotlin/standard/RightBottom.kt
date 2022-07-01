package standard

import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.BOTTOM_RIGHT
import ru.cristalix.uiengine.utility.text

context(KotlinMod)
class RightBottom {
    private val right = text {
        origin = BOTTOM_RIGHT
        align = BOTTOM_RIGHT
        shadow = true
        offset.y -= 15
        offset.x -= 3
    }

    init {
        UIEngine.overlayContext + right

        registerChannel("func:bottom") {
            right.content = NetUtil.readUtf8(this)
        }
    }
}
