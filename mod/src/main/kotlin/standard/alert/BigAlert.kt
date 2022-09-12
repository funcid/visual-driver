package standard.alert

import dev.xdark.feder.NetUtil
import lazyText
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.BOTTOM
import ru.cristalix.uiengine.utility.text

class BigAlert {
    private val title: TextElement by lazyText {
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

        mod.registerChannel("ilisov:bigtitle") {
            title.content = NetUtil.readUtf8(this)
            title.enabled = true

            UIEngine.schedule(6) {
                title.enabled = false
            }
        }
    }
}
