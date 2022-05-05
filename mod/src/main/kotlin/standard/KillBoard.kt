package standard

import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.RIGHT
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.text

class KillBoard(
    private val text: String, val box: RectangleElement = rectangle {
        size = V3(UIEngine.clientApi.fontRenderer().getStringWidth(text) + 5.0, 12.0)
        color = Color(6, 6, 6, 0.5)
        offset = V3(10.0, 0.0)
        align = RIGHT
        +text {
            content = text
            align = CENTER
            shadow = true
            origin = V3(0.5, 0.5)
        }
    }
)
