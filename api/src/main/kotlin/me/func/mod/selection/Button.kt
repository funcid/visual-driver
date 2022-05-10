package me.func.mod.selection

import me.func.mod.util.itemToTexture
import me.func.protocol.gui.StoragePosition
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun interface ButtonClickHandler {
    fun handle(player: Player, index: Int, button: Button)
}

class Button(texture: String = "", price: Long = -1, title: String = "", description: String = "") : StoragePosition(
    texture, price, title, description
) {

    constructor(item: ItemStack, price: Long, title: String, description: String) :
            this(itemToTexture(item), price, title, description)

    var onClick: ButtonClickHandler? = null

    fun item(item: ItemStack) = apply { texture = itemToTexture(item) }

    fun onClick(click: ButtonClickHandler) = apply { onClick = click }
}