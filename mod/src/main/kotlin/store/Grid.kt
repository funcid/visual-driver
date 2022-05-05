package store

import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.CENTER
import store.signage.BUTTON_SIZE

// Сеточка по которой, кнопки располагаются, в меню
fun grid(vararg children: AbstractElement) = Grid().also { it.addChild(*children) }

inline fun grid(builder: Grid.() -> Unit) = Grid().also(builder)

class Grid(
    cellSizeX: Double = BUTTON_SIZE,
    cellSizeY: Double = BUTTON_SIZE,
    borderWidth: Double = MARGIN,
    columns: Int = 3
) : RectangleElement() {
    var cellSizeX: Double = cellSizeX
        set(value) {
            field = value
            update()
        }

    var cellSizeY: Double = cellSizeY
        set(value) {
            field = value
            update()
        }

    var borderWidth: Double = borderWidth
        set(value) {
            field = value
            update()
        }

    var columns: Int = columns
        set(value) {
            field = value
            update()
        }

    var scrollPosition = 0.0

    init {
        align = CENTER
        origin = CENTER
    }

    // Обновление кнопок, при добавлении новой кнопки, все остальные располагаются правильно.
    fun update() {

        var i = 0

        for (e in children) {
            if (e !is RectangleElement) continue
            val w = (e.size.x / BUTTON_SIZE).toInt()
            e.offset.x = i % columns * (BUTTON_SIZE + MARGIN)
            e.offset.y = i / columns * (BUTTON_SIZE + MARGIN)
            i += w
        }

        size.x = BUTTON_SIZE * columns + MARGIN * (columns - 1)
        size.y = BUTTON_SIZE * ((i + 2) / 3) + MARGIN * ((i + 2) / 3 - 1)

        if (lastParent != null) {
            val o = -(lastParent!!.size.y - 40 - size.y) / 2.0
            scrollPosition = if (o <= 0) 0.0 else scrollPosition.coerceIn(-o, o)
            offset.y = scrollPosition
        }
    }
}
