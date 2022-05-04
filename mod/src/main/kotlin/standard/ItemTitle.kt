package standard

import dev.xdark.clientapi.item.ItemTools
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

object ItemTitle {

    init {
        val title = text {
            content = ""
            beforeRender = {
                GlStateManager.disableDepth()
            }
            align = V3(0.5, 0.6)
            origin = BOTTOM
            scale = V3(0.0, 0.0, 0.0)
            shadow = true
        }
        val subtitle = text {
            content = ""
            align = V3(0.5, 0.6)
            afterRender = {
                GlStateManager.enableDepth()
            }
            offset.y = 1.0
            origin = TOP
            shadow = true
        }

        UIEngine.overlayContext.addChild(title, subtitle)

        Standard::class.java.mod.registerChannel("func:drop-item") {
            UIEngine.clientApi.overlayRenderer().displayItemActivation(ItemTools.read(this))
            title.content = NetUtil.readUtf8(this)
            subtitle.content = NetUtil.readUtf8(this)
            val duration = readDouble()

            title.animate(duration / 2, Easings.ELASTIC_OUT) {
                scale.x = 4.0
                scale.y = 4.0
            }
            subtitle.animate(duration / 4, Easings.ELASTIC_OUT) {
                scale.x = 2.0
                scale.y = 2.0
            }
            UIEngine.schedule(duration) {
                title.animate(0.25) {
                    scale.x = 0.0
                    scale.y = 0.0
                }
                subtitle.animate(0.25) {
                    scale.x = 0.0
                    scale.y = 0.0
                }
            }
        }
    }

}