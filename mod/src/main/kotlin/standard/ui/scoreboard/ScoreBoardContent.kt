package standard.ui.scoreboard

import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.TOP
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.text

class ScoreBoardContent(value: String) : RectangleElement() {

    val valueText = +text {
        content = value
        origin = CENTER
        align = CENTER
        shadow = true
    }

    init {
        size = V3(100.0, valueText.lineHeight)
        origin = TOP
        align = TOP
    }
}