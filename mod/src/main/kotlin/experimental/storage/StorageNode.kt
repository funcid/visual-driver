package experimental.storage

import dev.xdark.clientapi.opengl.GlStateManager
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*

abstract class StorageNode<T : AbstractElement>(
    var price: Long = -1,
    var title: String,
    var description: String,
    open var icon: T,
    var hint: String? = null
) {

    var bundle: CarvedRectangle? = null
    var titleElement: TextElement? = null
    var descriptionElement: TextElement? = null
    var hintElement: TextElement? = null

    fun createHint(sized: V3, default: String) = carved {
        carveSize = 2.0
        size = sized
        color = Color(74, 140, 236, 1.0)
        color.alpha = 0.0
        beforeRender {
            GlStateManager.disableDepth()
        }
        afterRender {
            GlStateManager.enableDepth()
        }

        hintElement = +text {
            origin = CENTER
            align = CENTER
            color = WHITE
            shadow = true
            color.alpha = 0.0
            content = hint ?: default
            scale = V3(1.5, 1.5, 1.5)
        }
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