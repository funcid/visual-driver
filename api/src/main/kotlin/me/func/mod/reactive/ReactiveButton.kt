package me.func.mod.reactive

import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.data.Sprites
import me.func.mod.ui.menu.MenuManager.reactive
import me.func.mod.util.warn
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.color.RGB
import me.func.protocol.ui.menu.Button
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

fun interface ButtonClickHandler {
    fun handle(player: Player, index: Int, button: ReactiveButton)
}

class ReactiveButton : Button() {

    override var hover: String? = null
        set(value) {
            if (value != field) reactive { byte(5).string(value!!) }
            field = value
        }

    override var texture: String? = null
        set(value) {
            if (value != field) reactive { byte(0).string(value!!) }
            field = value
        }

    override var title: String = ""
        set(value) {
            if (value != field) reactive { byte(2).string(value) }
            field = value
        }

    override var description: String = ""
        set(value) {
            if (value != field) reactive { byte(3).string(value) }
            field = value
        }

    var onClick: ButtonClickHandler? = null

    var onLeftClick: ButtonClickHandler? = null

    var onRightClick: ButtonClickHandler? = null

    var onMiddleClick: ButtonClickHandler? = null

    var item: ItemStack? = null
        set(value) {
            if (value != field) value?.let { reactive { byte(1).item(value) } }
            field = value
        }

    override var hint: String? = null
        set(value) {
            if (value != field) reactive { byte(4).string(value!!) }
            field = value
        }

    override var backgroundColor: RGB = GlowColor.BLUE
        set(value) {
            if (value != field) reactive { byte(6).rgb(value) }
            field = value
        }

    override var enabled: Boolean = true
        set(value) {
            if (value != field) reactive { byte(7).boolean(value) }
            field = value
        }

    override var vault: String? = ""

    override var command: String? = ""

    override var price: Long = -1
        set(value) {
            if (value != field) reactive { byte(8).long(value) }
            field = value
            priceText = value.toString()
        }
    override var priceText: String = ""
        set(value) {
            if (value != field) reactive { byte(9).string(value) }
            field = value
        }

    override var sale = 0

    fun sale(percent: Int) = apply {
        if (percent !in 0..100) {
            warn("Sale percent must be between 0 and 100!")
            return this
        }
        sale = percent
    }

    fun title(title: String) = apply { this.title = title }

    fun color(color: RGB) = apply { this.backgroundColor = color }

    fun special(boolean: Boolean) = apply { this.backgroundColor = if (boolean) GlowColor.ORANGE else GlowColor.BLUE }

    fun enabled(enabled: Boolean) = apply { this.enabled = enabled }

    fun withSale() = ((100.0 - sale) / 100.0 * price).toInt()

    fun material(material: Material) = apply { item = ItemStack(material) }

    @JvmOverloads
    fun item(current: ItemStack, desc: Boolean = false) = apply {
        item = current
        if (desc) hover(current.lore ?: Collections.emptyList())
    }

    fun hover(text: Collection<String>) = hover(*text.toTypedArray())

    fun hover(text: String) = apply { hover = text }

    fun hover(vararg text: String) = apply { hover = text.joinToString("\n") }

    fun onClick(click: ButtonClickHandler) = apply { onClick = click }

    fun onLeftClick(click: ButtonClickHandler) = apply { onLeftClick = click }

    fun onRightClick(click: ButtonClickHandler) = apply { onRightClick = click }

    fun onMiddleClick(click: ButtonClickHandler) = apply { onMiddleClick = click }

    fun hint(hint: String) = apply { this.hint = hint }

    fun texture(texture: String) = apply { this.texture = texture }

    fun texture(sprite: Sprites) = apply { this.texture = sprite.path() }

    fun vault(vault: String) = apply { this.vault = vault }

    fun command(command: String) = apply { this.command = command }

    fun description(vararg desc: String) = apply { this.description = desc.joinToString("\n") }

    fun description(desc: List<String>) = description(*desc.toTypedArray())

    @JvmOverloads
    fun price(price: Long, text: String = "") = apply {
        this.price = price
        this.priceText = text.ifEmpty { price.toString() }
    }

    fun write(transfer: ModTransfer) {
        val isItem = item != null
        transfer.boolean(isItem)

        if (isItem) transfer.item(item!!)
        else transfer.string(texture ?: "")

        transfer.long(price)
        transfer.string(priceText)
        transfer.string(title)
        transfer.string(description)
        transfer.string(hint ?: "")
        transfer.string(hover ?: "")
        transfer.string(command ?: "")
        transfer.string(vault ?: "")
        transfer.rgb(backgroundColor)
        transfer.boolean(enabled)
        transfer.integer(sale)
    }

    fun copy(): ReactiveButton = ReactiveButton().also {
        it.hover = hover
        it.texture = texture
        it.title = title
        it.description = description
        it.onClick = onClick
        it.onLeftClick = onLeftClick
        it.onRightClick = onRightClick
        it.onMiddleClick = onMiddleClick
        it.item = item
        it.hint = hint
        it.price = price
        it.priceText = priceText
        it.sale = sale
        it.backgroundColor = backgroundColor
        it.enabled = enabled
        it.command = command
        it.vault = vault
    }

}
