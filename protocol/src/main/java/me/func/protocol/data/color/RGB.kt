package me.func.protocol.data.color

interface RGB {

    var red: Int
    var green: Int
    var blue: Int

    fun toRGB(red: Int, green: Int, blue: Int): Int {
        var rgb: Int = red
        rgb = (rgb shl 8) + green
        rgb = (rgb shl 8) + blue
        return rgb
    }

    fun toRGB(): Int

}