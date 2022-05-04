package store.signage

import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import store.grid

// Экран вывесок, в котором располагаются все кнопки
class SignageScreen(vararg button: SignageButton) : ContextGui() {

    val contentWrapper = grid(*button)

    val body = +rectangle {
        size.x = 250.0
        size.y = 20000.0
        origin = LEFT
        align = LEFT
        color = Color(0, 0, 0, 0.86)
        beforeRender {
            size.y = this@SignageScreen.size.y
        }
        color.alpha = 0.82
        addChild(text {
            content = "< [ ESC ] Назад"
            color.alpha = 0.5
            offset = V3(4.0, 4.0)
            onHover {
                animate(0.3, Easings.QUART_OUT) {
                    color.alpha = if (hovered) 0.8 else 0.5
                }
            }
        })
        addChild(contentWrapper)
    }
}
