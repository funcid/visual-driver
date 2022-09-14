package standard.storage

import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import standard.storage.button.StorageNode
import java.util.*

interface AbstractMenu {

    var uuid: UUID

    var info: String

    var storage: MutableList<StorageNode<*>>

    fun open()

    fun close()

    fun getInformationBlock(): CarvedRectangle

    fun generateInformation() = carved {
        val scalar = 15.0
        align = TOP_RIGHT
        origin = TOP_RIGHT

        var hasHovered = false

        onHover {
            if (!hasHovered && hovered) {
                hasHovered = !hasHovered
                animate(0.1, Easings.QUAD_OUT) {
                    size = V3(scalar + 4, scalar + 4)
                }
            } else if (hasHovered && !hovered) {
                hasHovered = !hasHovered
                animate(0.1, Easings.QUAD_OUT) {
                    size = V3(scalar, scalar)
                }
            }
        }
        offset = V3(-scalar, scalar, scalar)
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