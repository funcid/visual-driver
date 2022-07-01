package experimental.storage

import dev.xdark.clientapi.opengl.GlStateManager
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.WHITE
import ru.cristalix.uiengine.utility.carved
import ru.cristalix.uiengine.utility.text

abstract class StorageNode<T : AbstractElement>(
    @JvmField var price: Long = -1,
    @JvmField var title: String,
    @JvmField var description: String,
    @JvmField var hoverText: String,
    open var icon: T,
    @JvmField var hint: String? = null
) {

    @JvmField var bundle: CarvedRectangle? = null
    @JvmField var titleElement: TextElement? = null
    @JvmField var descriptionElement: TextElement? = null
    @JvmField var hintElement: TextElement? = null

    fun createHint(sized: V3, default: String) = carved {
        carveSize = 2.0
        size = sized
        color = Color(74, 140, 236, 1.0)
        color.alpha = 0.0
        beforeRender { GlStateManager.disableDepth() }
        afterRender { GlStateManager.enableDepth() }

        if (hintElement == null) {
            hintElement = text {
                origin = CENTER
                align = CENTER
                color = WHITE
                shadow = true
                color.alpha = 0.0
                content = hint ?: default
                scale = V3(1.5, 1.5, 1.5)
            }
        }
        +hintElement!!
    }

    fun optimizeSpace(length: Double = (bundle?.size?.x ?: 200.0) - (bundle?.size?.y ?: 100.0)) {
        if (bundle == null || descriptionElement == null) return
        val words = description.split(" ")
        descriptionElement!!.content = "§f"
        words.forEach { word ->
            val line = descriptionElement!!.content.split("\n").last()
            val new = line + word
            val color = line.split("§").last().first()
            if (UIEngine.clientApi.fontRenderer().getStringWidth(new.drop(new.count { it == '§' } * 2)) > length)
                descriptionElement!!.content += "\n§$color"
            descriptionElement!!.content += "$word "
        }
    }

    abstract fun scaling(scale: Double): T

}
