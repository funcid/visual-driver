package standard.storage

import Main.Companion.externalManager
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*

class TextedIcon(text: String, icon: String, textLeft: Boolean = true) : RectangleElement() {
    @JvmField
    val title = +text {
        origin = if (textLeft) LEFT else RIGHT
        align = if (textLeft) LEFT else RIGHT
        content = text
        color = WHITE
    }

    private val preIcon: AbstractElement = if (icon.contains(":")) rectangle {
        size = V3(title.lineHeight - 2, title.lineHeight - 2)
        textureLocation = externalManager.load(icon)
    } else text {
        content = icon
        scale = title.scale
    }

    private val image = +preIcon.apply {
        origin = if (textLeft) RIGHT else LEFT
        align = if (textLeft) RIGHT else LEFT
        color = WHITE
    }

    init {
        super.size = V3(image.size.x + title.size.x + 2, title.lineHeight)
    }
}
