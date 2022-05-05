package store.product

import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.Easings
import ru.cristalix.uiengine.utility.LEFT
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.text

// Экран продуктов, при нажатии на определённую категорию, у вас открывается определённые вещи
class ProductScreen : ContextGui() {

    private val body = +rectangle {
        size.x = 250.0
        size.y = 20000.0
        origin = LEFT
        align = LEFT
        color = Color(0, 0, 0, 0.86)
        beforeRender {
            size.y = this@ProductScreen.size.y
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
    }
}