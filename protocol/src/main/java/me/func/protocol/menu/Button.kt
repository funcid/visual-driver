package me.func.protocol.menu

open class Button(
    open var hover: String? = "",
    open var texture: String? = "",
    open var title: String? = "",
    open var description: String? = "",
    open var hint: String? = "",
    open var special: Boolean = false,
    open var price: Long = -1,
    open var command: String? = ""
)