package me.func.mod.selection

import me.func.mod.conversation.ModTransfer
import me.func.mod.selection.MenuManager.reactive
import me.func.mod.util.nbt
import me.func.mod.util.warn
import me.func.protocol.gui.StoragePosition
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun interface ButtonClickHandler {
    fun handle(player: Player, index: Int, button: Button)
}

class Button {

    @JvmOverloads
    constructor(title: String, price: Long = -1, vararg description: String) : this(title, price, description.joinToString("\n"))

    @JvmOverloads
    constructor(title: String, price: Long = -1, description: List<String>) : this(title, price, *description.toTypedArray())

    @JvmOverloads
    constructor(title: String, price: Long = -1, description: String) : this() {
        this.title = title
        this.price = price
        this.description = description
    }

    @JvmOverloads
    constructor(itemStack: ItemStack, title: String, description: String = "", price: Long = -1) : this() {
        item = itemStack
        this.title = title
        this.price = price
        this.description = description
    }

    constructor()

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

    fun withSale() = ((100.0 - sale) / 100.0 * price).toInt()

    fun material(material: Material) = apply { item = ItemStack(material) }

    fun item(current: ItemStack) = apply { item = current }

    fun onClick(click: ButtonClickHandler) = apply { onClick = click }

    fun hint(hint: String) = apply { this.hint = hint }

    fun write(transfer: ModTransfer) {
        val isItem = item != null
        transfer.boolean(isItem)

        if (isItem) transfer.item(item!!)
        else transfer.string(texture ?: "")

        transfer.long(price)
        transfer.string(title ?: "")
        transfer.string(description ?: "")
    }
}