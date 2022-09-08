package me.func.protocol.menu

open class Button {
    open val hover: String? = ""
    open val texture: String? = ""
    open val title: String? = ""
    open val description: String? = ""
    open val hint: String? = ""
    open var special: Boolean = false
    open var price: Long = -1
    open var command: String? = ""
}