package experimental.storage

import dev.xdark.feder.NetUtil
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.Flex

abstract class StorageNode(
    var price: Long,
    var title: String,
    var description: String,
    var hint: String = ""
) : AbstractElement() {

    var fullElement: CarvedRectangle? = null

    fun applyText() {
        if (fullElement == null) return
        val lore = (fullElement!!.children.first { it is Flex } as Flex).children[1] as TextElement
        val words = lore.content.split(" ")
        lore.content = "§7"
        words.forEach {
            val line = lore.content.split("\n").last() + it
            val color = line.split("§").last().first()
            if (UIEngine.clientApi.fontRenderer().getStringWidth(line) > fullElement!!.size.x - fullElement!!.size.y)
                lore.content += "\n§$color"
            lore.content += "$it "
        }
    }

    abstract fun withPadding(padding: Double): AbstractElement

}