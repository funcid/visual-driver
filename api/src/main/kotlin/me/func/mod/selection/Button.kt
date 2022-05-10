package me.func.mod.selection

import me.func.protocol.gui.StoragePosition
import java.util.function.BiConsumer

class Button(texture: String = "", price: Int = -1, title: String = "", description: String = "") : StoragePosition(
    texture, price, title, description
) {
    var onClick: BiConsumer<Int, Button>? = null

    fun handleClick(click: BiConsumer<Int, Button>) = apply { onClick = click }
}