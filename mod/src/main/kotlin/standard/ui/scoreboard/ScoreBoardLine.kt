package standard.ui.scoreboard

import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*

class ScoreBoardLine(name: String, value: String, action: ScoreBoardLine.() -> Unit) : RectangleElement() {

    val nameText = +text {
        content = name
        origin = LEFT
        align = LEFT
        shadow = true
    }

    val valueText = +text {
        content = value
        origin = RIGHT
        align = RIGHT
        shadow = true
        color = Color(74, 140, 236)
    }

    init {
        size = V3(100.0, nameText.lineHeight)
        origin = TOP
        align = TOP

        action()
    }
}