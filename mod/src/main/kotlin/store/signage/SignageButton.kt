package store.signage

import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.BOTTOM
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.Easings
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.item
import ru.cristalix.uiengine.utility.text
import store.util.BUTTON_BLUE

// Создание кнопок, которое бы стоило обобщить
inline fun button(setup: SignageButton.() -> Unit) = SignageButton().also(setup)

const val BUTTON_SIZE = 60.0

class SignageButton : RectangleElement() {

    val icon = item {
        origin = CENTER
        align = CENTER
        offset.y = -4.0
        scale = V3(2.0, 2.0, 1.0)
    }

    val title = text {
        origin = BOTTOM
        align = BOTTOM
        offset.y = -2.0
    }

    init {
        size.x = BUTTON_SIZE
        size.y = BUTTON_SIZE
        color = BUTTON_BLUE
        color.alpha = 0.28
        addChild(icon, title)
        onHover {
            animate(0.3, Easings.QUART_OUT) {
                color.alpha = if (hovered) 1.0 else 0.28
            }
        }
    }

}