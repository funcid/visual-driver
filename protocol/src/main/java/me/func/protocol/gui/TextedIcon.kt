package gui

import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*

class TextedIcon(text: String, icon: ResourceLocation, textLeft: Boolean = true) : RectangleElement() {
    val title = +text {
        origin = if (textLeft) LEFT else RIGHT
        align = if (textLeft) LEFT else RIGHT
        content = text
        color = WHITE
    }
    private val image = +rectangle {
        textureLocation = icon
        origin = if (textLeft) RIGHT else LEFT
        align = if (textLeft) RIGHT else LEFT
        size = V3(title.lineHeight - 2, title.lineHeight - 2)
        color = WHITE
    }

    init {
        super.size = V3(image.size.x + title.size.x + 2, title.lineHeight)
    }
}