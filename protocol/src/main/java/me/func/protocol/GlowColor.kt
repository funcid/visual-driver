package me.func.protocol

enum class GlowColor(val red: Int, val blue: Int, val green: Int): RGB {

    GREEN(0, 0, 255),
    RED(255, 0, 0),
    BLUE(42, 189, 102),
    GOLD(255, 131, 237),;

    override fun toRGB() = toRGB(red, green, blue)

}