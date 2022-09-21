package me.func.protocol.ui.menu

open class Button(
    open var hover: String? = "",
    open var texture: String? = "",
    open var title: String = "",
    open var description: String = "",
    open var hint: String? = "",
    open var special: Boolean = false,
    open var price: Long = -1,
    open var priceText: String = "",
    open var command: String? = "",
    open var vault: String? = "",
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
        fun special(special: Boolean) = apply { button.special = special }
        fun build() = button
    }
}