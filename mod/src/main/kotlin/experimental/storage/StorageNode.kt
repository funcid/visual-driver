package experimental.storage

import dev.xdark.clientapi.opengl.GlStateManager
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*

abstract class StorageNode<T : AbstractElement>(
    @JvmField var price: Long = -1,
    @JvmField var title: String,
    @JvmField var description: String,
    var hoverText: String,
    open var icon: T,
    var special: Boolean = false,
    var hint: String? = null
) {

    var bundle: CarvedRectangle? = null
    var titleElement: TextElement? = null
    var descriptionElement: TextElement? = null
    var hintElement: TextElement? = null
    var hintContainer: CarvedRectangle? = null

    fun createHint(sized: V3, default: String) = hintContainer ?: carved {
        carveSize = 2.0
        size = sized
        color = if (special) Color(255,157,66, 1.0) else Color(74, 140, 236, 1.0)
        color.alpha = 0.0
        beforeRender { GlStateManager.disableDepth() }
        afterRender { GlStateManager.enableDepth() }

        hintElement = +text {
            origin = CENTER
            align = CENTER
            color = WHITE
            shadow = true
            color.alpha = 0.0
            content = hint ?: default
            scale = V3(1.5, 1.5, 1.5)
        }
    }.apply { hintContainer = this }

    fun optimizeSpace(length: Double = (bundle?.size?.x ?: 200.0) - (bundle?.size?.y ?: 100.0)) {
        if (bundle == null || descriptionElement == null) return
        val words = description.split(" ")
        val font = UIEngine.clientApi.fontRenderer()

        descriptionElement!!.content = lineStart
        words.forEach { word ->
            val line = descriptionElement!!.content.split("\n").last()
            val new = line + word
            val color = line.split("§").last().first()
            if (line != lineStart && font.getStringWidth(new.drop(new.count { it == '§' } * 2).replace("\n", "")) > length)
                descriptionElement!!.content += "\n§$color"
            descriptionElement!!.content += "$word "
        }
    }

    abstract fun scaling(scale: Double): T

    private companion object {
        private const val lineStart = "§f"
    }
}
