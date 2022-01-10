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

        App::class.mod.registerChannel("func:bottom-right") {
            right.content = NetUtil.readUtf8(this)
            right.offset.y = -15.0 * right.content.split("\n").size
        }
    }

}