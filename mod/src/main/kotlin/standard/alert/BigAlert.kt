package standard.alert

import dev.xdark.feder.NetUtil
import lazyText
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.Task
import ru.cristalix.uiengine.utility.BOTTOM

class BigAlert {
    private var task: Task? = null

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
            val duration = readDouble()
            title.content = NetUtil.readUtf8(this)
            title.enabled = true
            task?.cancelled = true

            task = UIEngine.schedule(duration) {
                title.enabled = false
            }
        }
    }
}
