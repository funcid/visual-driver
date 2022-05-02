import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.BOTTOM_RIGHT
import ru.cristalix.uiengine.utility.text

object RightBottom {

    private val right = text {
        origin = BOTTOM_RIGHT
        align = BOTTOM_RIGHT
        shadow = true
        offset.y -= 15
        offset.x -= 3
    }

    init {
        UIEngine.overlayContext + right

        Standard::class.java.mod.registerChannel("func:bottom") {
            right.content = NetUtil.readUtf8(this)
        }
    }

}