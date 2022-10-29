package standard.storage.button

import asColor
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.color.RGB
import me.func.protocol.ui.menu.Button
import ru.cristalix.uiengine.ClickEvent
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*
import standard.storage.AbstractMenu
import standard.storage.TextedIcon
import standard.storage.menu.MenuManager

abstract class StorageNode<T : AbstractElement>(
    open var icon: T,
    override var price: Long = -1,
    override var priceText: String = "",
    override var title: String = "",
    override var description: String = "",
    override var hint: String? = null,
    var hoverText: String = "",
    override var command: String? = null,
    override var backgroundColor: RGB = GlowColor.BLUE,
    override var enabled: Boolean = true,
    override var vault: String? = null,
    override var sale: Int = 0
) : Button() {

    var bundle: CarvedRectangle? = null
    var titleElement: TextElement? = null
    var descriptionElement: TextElement? = null
    var hintElement: TextElement? = null
    var hintContainer: CarvedRectangle? = null
    var priceElement: TextedIcon? = null

    fun createHint(sized: V3, default: String) = hintContainer ?: carved {
        carveSize = 2.0
        size = sized
        color = backgroundColor.asColor()
        color.alpha = 0.0
        beforeRender { GlStateManager.disableDepth() }
        afterRender { GlStateManager.enableDepth() }

        hintElement = +text {
            origin = CENTER
            align = CENTER
            color = WHITE
            color.alpha = 0.0
            content = hint ?: default
            scale = V3(2.0, 2.0, 2.0)
        }
    }.apply { hintContainer = this }

    fun click(menu: AbstractMenu, event: ClickEvent) {

        if (MenuManager.isMenuClickBlocked()) return

        val key = menu.storage.indexOf(this@StorageNode)

        if (command.isNullOrEmpty()) {
            UIEngine.clientApi.clientConnection().sendPayload("storage:click", Unpooled.buffer().apply {
                NetUtil.writeUtf8(this, menu.uuid.toString())
                writeInt(key)
                writeInt(event.button.ordinal)
            })
            return
        }
        val command = "/" + if (command?.startsWith("/") == true) command?.drop(1) else command

        UIEngine.clientApi.chat().sendChatMessage("$command $key")
    }

    fun optimizeSpace(length: Double = (bundle?.size?.x ?: 200.0) - (bundle?.size?.y ?: 100.0)) {
        if (bundle == null || descriptionElement == null) return

        val words = description.split(" ")

        descriptionElement!!.content = lineStart

        words.forEach { word ->
            val line = descriptionElement!!.content.split("\n").last()
            val new = line + word
            val color = line.split("§").last().first()

            if (new.getRealWidth() * 0.75 > length) {
                descriptionElement!!.content += "\n§$color"
            }
            descriptionElement!!.content += "$word "
        }
    }

    private fun String.getRealWidth(): Int {
        val font = UIEngine.clientApi.fontRenderer()
        val colorChars = count { it == '§' } * 2
        val realString = drop(colorChars).replace("\n", "")

        return font.getStringWidth(realString)
    }

    abstract fun scaling(scale: Double): T

    private companion object {
        private const val lineStart = "§f"
    }
}
