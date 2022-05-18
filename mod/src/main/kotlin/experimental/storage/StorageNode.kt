package experimental.storage

import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement

abstract class StorageNode<T : AbstractElement>(
    var price: Long = -1,
    var title: String,
    var description: String,
    open var icon: T,
    var hint: String = ""
) {

    var bundle: CarvedRectangle? = null
    var titleElement: TextElement? = null
    var descriptionElement: TextElement? = null

    fun optimizeSpace(length: Double = (bundle?.size?.x ?: 200.0) - (bundle?.size?.y ?: 100.0)) {
        if (bundle == null || descriptionElement == null) return
        val words = description.split(" ")
        descriptionElement!!.content = "§7"
        words.forEach {
            val line = descriptionElement!!.content.split("\n").last() + it
            val color = line.split("§").last().first()
            if (UIEngine.clientApi.fontRenderer().getStringWidth(line) > length)
                descriptionElement!!.content += "\n§$color"
            descriptionElement!!.content += "$it "
        }
    }

    abstract fun scaling(scale: Double): T

}