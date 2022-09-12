package standard.daily

import dev.xdark.clientapi.item.ItemStack
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

class Day(
    @JvmField val day: Int,
    private val icon: ItemStack,
    @JvmField val name: String,
    private val claimedStatus: String,
    private val claimed: Boolean
) : CarvedRectangle() {
    private var originalX = 0.0

    var title = text {
        offset.y += 5
        origin = TOP
        align = TOP
        shadow = true
        content = "$day\nдень"
    }

    var iconBox = item {
        origin = CENTER
        align = CENTER
        scale = V3(2.0, 2.0, 2.0)
        stack = icon
    }

    var state = text {
        offset.y -= 5
        origin = BOTTOM
        align = BOTTOM
        color = if (claimed) {
            Color(100, 100, 100, 1.0)
        } else {
            Color(224, 118, 20, 1.0)
        }
        shadow = true
        content = claimedStatus
    }

    init {
        offset.x = -(4 - day) * 55.0
        origin = CENTER
        align = CENTER
        size = V3(50.0, 150.0)
        color = Color(0, 0, 0, if (claimed) 0.62 else 0.3)
        addChild(title, iconBox, state)
        originalX = offset.x
    }

    fun move(scale: Int) {
        if (originalX == offset.x) {
            animate(0.1) {
                offset.x += 10 * scale
            }
        }
    }

    fun normalize() {
        animate(0.07) {
            offset.x = originalX
        }
    }
}
