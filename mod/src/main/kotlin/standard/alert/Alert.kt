package standard.alert

import dev.xdark.feder.NetUtil.readUtf8
import lazyCarved
import lazyText
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

private const val VECTOR = 65

class Alert {

    private val dayTitle: TextElement by lazyText {
        align = CENTER
        origin = CENTER
        scale = V3(1.5, 1.5)
        color = WHITE
        shadow = true
        content = "Название игры"
    }

    private val lore: TextElement by lazyText {
        align = CENTER
        origin = CENTER
        scale = V3(0.75, 0.75)
        color = WHITE
        content = "Первая строка описания\nВторая строка описания"
    }

    private val loreBox: CarvedRectangle by lazyCarved {
        align = BOTTOM
        origin = BOTTOM
        size = V3(180.0, 18.0)
        offset.y += size.y
        color = Color(0, 0, 0, 0.62)
        +lore
    }

    private val dayTitleBox: CarvedRectangle by lazyCarved {
        align = TOP
        origin = TOP

        offset.y += -VECTOR

        color = Color(42, 102, 189, 0.86)
        size = V3(160.0, 32.0)

        +dayTitle
        +loreBox
    }

    init {
        UIEngine.overlayContext + dayTitleBox

        var block = false

        mod.registerChannel("func:alert") {
            dayTitle.content = readUtf8()
            lore.content = readUtf8()

            if (block) return@registerChannel

            val duration = readDouble()
            block = true

            dayTitleBox.animate(0.45, Easings.BACK_BOTH) {
                offset.y += VECTOR + 50
            }

            UIEngine.schedule(duration + 0.45) {
                dayTitleBox.animate(0.2, Easings.BACK_IN) {
                    offset.y -= (VECTOR + 50)
                }
            }

            UIEngine.schedule(duration + 0.65) {
                block = false
            }
        }
    }
}
