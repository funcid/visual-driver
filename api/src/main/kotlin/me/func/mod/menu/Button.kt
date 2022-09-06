package me.func.mod.menu

import me.func.mod.conversation.ModTransfer
import me.func.mod.data.Sprites
import me.func.mod.menu.MenuManager.reactive
import me.func.mod.util.nbt
import me.func.mod.util.warn
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

fun interface ButtonClickHandler {
    fun handle(player: Player, index: Int, button: Button)
}

class Button {

    var hover: String? = null
        set(value) {
            if (value != field) reactive { byte(5).string(value!!) }
            field = value
        }

    var texture: String? = null
        set(value) {
            if (value != field) reactive { byte(0).string(value!!) }
            field = value
        }

    var title: String? = null
        set(value) {
            if (value != field) reactive { byte(2).string(value!!) }
            field = value
        }

    var description: String? = null
        set(value) {
            if (value != field) reactive { byte(3).string(value!!) }
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

    var hint: String? = null
        set(value) {
            if (value != field) reactive { byte(4).string(value!!) }
            field = value
        }

    var special: Boolean = false

    var price: Long = -1
    private var sale = 0

    fun sale(percent: Int) = apply {
        if (item == null) {
            warn("Cannot add sale to non-item button! Sorry :(")
            return this
        } else if (percent !in 0..100) {
            warn("Sale percent must be between 0 and 100!")
            return this
        }
        item = item?.nbt("sale", percent.toString())
        sale = percent
    }

    fun title(title: String) = apply { this.title = title }

    fun special(special: Boolean) = apply { this.special = special }

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

    fun description(vararg desc: String) = apply { this.description = desc.joinToString("\n") }

    fun description(desc: List<String>) = description(*desc.toTypedArray())

    fun price(price: Long) = apply { this.price = price }

    fun write(transfer: ModTransfer) {
        val isItem = item != null
        transfer.boolean(isItem)

        if (isItem) transfer.item(item!!)
        else transfer.string(texture ?: "")

        transfer.long(price)
        transfer.string(title ?: "")
        transfer.string(description ?: "")
        transfer.string(hint ?: "")
        transfer.string(hover ?: "")
        transfer.boolean(special)
    }

    fun copy(): Button = Button().also {
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
        it.sale = sale
        it.special = special
    }
}
