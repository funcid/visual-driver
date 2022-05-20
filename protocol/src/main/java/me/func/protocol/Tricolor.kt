package me.func.protocol

open class Tricolor(override var red: Int, override var blue: Int, override var green: Int): RGB {

    constructor(hex: Int) : this(hex and 0xFF0000 shr 16, hex and 0xFF00 shr 8, hex and 0xFF)

    override fun toRGB() = toRGB(red, blue, green)

}