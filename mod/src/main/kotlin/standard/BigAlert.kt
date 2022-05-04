package standard

import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.BOTTOM
import ru.cristalix.uiengine.utility.text

object BigAlert {
    private val title = text {
        origin = BOTTOM
        align = BOTTOM
        offset.y -= 60
        shadow = true
        enabled = false
        scale.x = 2.0
        scale.y = 2.0
    }

    init {
        UIEngine.overlayContext.addChild(title)

        Standard::class.java.mod.registerChannel("ilisov:bigtitle") {
            title.content = NetUtil.readUtf8(this)
            title.enabled = true

            UIEngine.schedule(6) {
                title.enabled = false
            }
        }
    }
}