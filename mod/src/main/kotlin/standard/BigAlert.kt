package standard

import dev.xdark.feder.NetUtil
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.BOTTOM
import ru.cristalix.uiengine.utility.text
import ru.cristalix.clientapi.KotlinMod

context(KotlinMod)
class BigAlert {
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

        registerChannel("ilisov:bigtitle") {
            title.content = NetUtil.readUtf8(this)
            title.enabled = true

            UIEngine.schedule(6) {
                title.enabled = false
            }
        }
    }
}
