package standard.storage

import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

interface Information {

    var info: String

    fun getInformationBlock(): CarvedRectangle

    fun generateInformation() = carved {
        val scalar = 15.0
        align = TOP_RIGHT
        origin = TOP_RIGHT

        var hasHovered = false

        onHover {
            if (!hasHovered && hovered) {
                hasHovered = true
                animate(0.1, Easings.QUAD_OUT) {
                    size = V3(scalar + 4, scalar + 4)
                    offset = V3(-scalar + 2, scalar - 2)
                }
            } else if (hasHovered && !hovered) {
                hasHovered = false
                animate(0.1, Easings.QUAD_OUT) {
                    size = V3(scalar, scalar)
                    offset = V3(-scalar, scalar)
                }
            }
        }
        offset = V3(-scalar, scalar)
        size = V3(scalar, scalar)
        color = Color(42, 102, 189)
        +text {
            align = CENTER
            origin = CENTER
            color = WHITE
            content = "?"
        }
    }

}