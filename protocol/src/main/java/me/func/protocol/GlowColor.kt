package me.func.protocol

enum class GlowColor(val red: Int, val blue: Int, val green: Int) {

    GREEN(0, 0, 255),
    RED(255, 0, 0),
    BLUE(42, 189, 102),
    GOLD(255, 131, 237);


    fun toRGB(): Int {
        var rgb: Int = this.red
        rgb = (rgb shl 8) + this.green
        rgb = (rgb shl 8) + this.blue
        return rgb
    }
}