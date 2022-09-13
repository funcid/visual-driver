package me.func.protocol.data.color

enum class GlowColor(override var red: Int, override var blue: Int, override var green: Int): RGB {

    GREEN(0, 0, 255),
    RED(255, 0, 0),
    BLUE(42, 189, 102),
    GOLD(255, 131, 237),;

    override fun toRGB() = toRGB(red, green, blue)

}