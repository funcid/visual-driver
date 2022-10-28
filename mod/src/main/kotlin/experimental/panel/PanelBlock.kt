package experimental.panel

import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import java.util.*

data class PanelBlock(
    val uuid: UUID,
    val element: CarvedRectangle,
    val text: TextElement,
    val progress: CarvedRectangle,
    var currentProgress: Double
)