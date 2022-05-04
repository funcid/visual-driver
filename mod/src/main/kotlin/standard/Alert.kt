package standard

import ru.cristalix.clientapi.mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

object Alert {

    private const val VECTOR = 65

    private lateinit var dayTitle: TextElement
    private lateinit var lore: TextElement
    private lateinit var loreBox: RectangleElement

    private val dayTitleBox = rectangle {
        align = TOP
        origin = TOP

        offset.y += -VECTOR

        color = Color(42, 102, 189, 0.86)
        size = V3(160.0, 32.0)

        dayTitle = +text {
            align = CENTER
            origin = CENTER
            scale = V3(1.5, 1.5)
            color = WHITE
            shadow = true
            content = "Название игры"
        }
        loreBox = +rectangle {
            align = BOTTOM
            origin = BOTTOM
            size = V3(180.0, 18.0)
            offset.y += size.y
            color = Color(0, 0, 0, 0.62)

            lore = +text {
                align = CENTER
                origin = CENTER
                scale = V3(0.75, 0.75)
                color = WHITE
                content = "Первая строка описания\nВторая строка описания"
            }
        }
    }

    init {
        UIEngine.overlayContext + dayTitleBox

        Standard::class.java.mod.registerChannel("func:alert") {
            dayTitle.content = readUtf8()
            lore.content = readUtf8()

            val duration = readDouble()

            dayTitleBox.animate(0.45, Easings.BACK_BOTH) {
                offset.y += VECTOR + 50
            }

            UIEngine.schedule(duration + 0.45) {
                dayTitleBox.animate(0.2, Easings.BACK_IN) {
                    offset.y -= (VECTOR + 50)
                }
            }
        }
    }
}