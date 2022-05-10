package me.func.mod.selection

import me.func.protocol.gui.StoragePosition
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

    fun item(current: ItemStack) = apply { item = current }

    fun onClick(click: ButtonClickHandler) = apply { onClick = click }
}