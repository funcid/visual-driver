package me.func.protocol

open class Tricolor(open val red: Int, open val blue: Int, open val green: Int): RGB {

    override fun toRGB() = toRGB(red, blue, green)

}