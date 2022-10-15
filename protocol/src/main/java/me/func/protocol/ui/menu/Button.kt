package me.func.protocol.ui.menu

import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.color.RGB

open class Button(
    open var hover: String? = "",
    open var texture: String? = "",
    open var title: String = "",
    open var description: String = "",
    open var hint: String? = "",
    open var backgroundColor: RGB = GlowColor.BLUE,
    open var enabled: Boolean = true,
    open var price: Long = -1,
    open var priceText: String = "",
    open var command: String? = "",
    open var vault: String? = "",
    open var sale: Int = 0
) {
    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(val button: Button = Button()) {
        fun hover(hover: String) = apply { button.hover = hover }
        fun vault(vault: String) = apply { button.vault = vault }
        fun texture(texture: String) = apply { button.texture = texture }
        fun title(title: String) = apply { button.title = title }
        fun description(description: String) = apply { button.description = description }
        fun hint(hint: String) = apply { button.hint = hint }
        fun command(command: String) = apply { button.command = command }
        fun price(price: Long) = apply { button.price = price }
        fun price(text: String) = apply { button.priceText = text }
        fun color(color: RGB) = apply { button.backgroundColor = color }
        fun enabled(enabled: Boolean) = apply { button.enabled = enabled }
        fun sale(sale: Int) = apply { button.sale = sale }
        fun build() = button
    }
}