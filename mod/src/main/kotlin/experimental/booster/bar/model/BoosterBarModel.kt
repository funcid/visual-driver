package experimental.booster.bar.model

import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement

data class BoosterBarModel(
    var segments: List<BoosterSegmentModel>,
    var title: String,
    var subtitle: String,
    var isShowBackground: Boolean,
    var progress: Double,
    var titleElement: TextElement? = null,
    var subtitleElement: TextElement? = null,
    var backgroundElement: RectangleElement? = null
)