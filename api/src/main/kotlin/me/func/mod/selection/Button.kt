package me.func.mod.selection

import me.func.mod.util.nbt
import me.func.mod.util.warn
import me.func.protocol.gui.StoragePosition
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun interface ButtonClickHandler {
    fun handle(player: Player, index: Int, button: Button)
}

class Button(texture: String = "", price: Long = -1, title: String = "", description: String = "") : StoragePosition(
    texture, price, title, description
) {

    var item: ItemStack? = null

    var onClick: ButtonClickHandler? = null

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
}