package experimental.booster.bar.model

import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement

data class BoosterSegmentModel(
    val label: String,
    var progress: Double? = null,
    var backgroundElement: RectangleElement? = null,
    var progressElement: RectangleElement? = null,
    var labelElement: TextElement? = null,
)