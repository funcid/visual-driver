package store.confirm

import store.util.BUTTON_BLUE
import store.util.RED
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

// Экран подтверждения покупки, я его не доделал :c
class ConfirmScreen: RectangleElement() {

    val body = +rectangle {
        size.y = 94.5
        size.x = 243.0
        origin = CENTER
        align = CENTER
        offset.y += 50
        color = Color(0,0,0,0.86)
        +rectangle {
            size.y = 7.0
            size.x = 81.5
            color = BUTTON_BLUE
            align = BOTTOM_LEFT
            origin = BOTTOM_LEFT
            onHover {
                animate(0.3, Easings.QUART_OUT) {
                    color.alpha = if (hovered) 0.8 else 0.5
                }
            }
        }
        +rectangle {
            size.y = 7.0
            size.x = 81.5
            color = RED
            align = BOTTOM_RIGHT
            origin = BOTTOM_RIGHT
            onHover {
                animate(0.3, Easings.QUART_OUT) {
                    color.alpha = if (hovered) 0.8 else 0.5
                }
            }
        }
    }

}