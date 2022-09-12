package me.func.mod.menu

import dev.xdark.feder.NetUtil
import me.func.mod.conversation.ModTransfer
import me.func.mod.data.Sprites
import me.func.mod.menu.MenuManager.reactive
import me.func.mod.util.nbt
import me.func.mod.util.warn
import me.func.protocol.menu.Button
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

    override var special: Boolean = false

    override var vault: String? = ""

    override var price: Long = -1
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

    fun vault(vault: String) = apply { this.vault = vault }

    fun description(vararg desc: String) = apply { this.description = desc.joinToString("\n") }

    fun description(desc: List<String>) = description(*desc.toTypedArray())

    fun price(price: Long) = apply { this.price = price }

    fun write(transfer: ModTransfer) {
        val isItem = item != null
        transfer.boolean(isItem)

        if (isItem) transfer.item(item!!)
        else transfer.string(texture ?: "")

        transfer.long(price)
        transfer.string(title)
        transfer.string(description)
        transfer.string(hint ?: "")
        transfer.string(hover ?: "")
        transfer.string(command ?: "")
        transfer.string(vault ?: "")
        transfer.boolean(special)
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
        it.sale = sale
        it.special = special
        it.command = command
        it.vault = vault
    }
}