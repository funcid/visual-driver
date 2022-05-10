package me.func.mod.selection

import me.func.protocol.gui.StoragePosition
import org.bukkit.entity.Player

fun interface ButtonClickHandler {
    fun handle(player: Player, index: Int, button: Button)
}

class Button(texture: String = "", price: Int = -1, title: String = "", description: String = "") : StoragePosition(
    texture, price, title, description
) {
    var onClick: ButtonClickHandler? = null

    fun onClick(click: ButtonClickHandler) = apply { onClick = click }
}