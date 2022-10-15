package me.func.protocol.data.color

enum class GlowColor(override var red: Int, override var blue: Int, override var green: Int): RGB {

    BLUE_LIGHT(74, 140, 236),
    BLUE(42, 102, 189),
    BLUE_MIDDLE(39, 84, 149),
    BLUE_DARK(21, 53, 98),

    RED_LIGHT(231, 61, 75),
    RED(169, 25, 37),
    RED_MIDDLE(102, 19, 27),
    RED_DARK(74, 15, 21),

    YELLOW_LIGHT(255, 202, 66),
    YELLOW(239, 172, 0),
    YELLOW_MIDDLE(141, 104, 8),
    YELLOW_DARK(85, 63, 6),

    GREEN_LIGHT(73, 223, 115),
    GREEN(34, 174, 73),
    GREEN_MIDDLE(20, 98, 41),
    GREEN_DARK(4, 72, 22),

    ORANGE_LIGHT(255, 157, 66),
    ORANGE(224, 118, 20),
    ORANGE_MIDDLE(150, 74, 9),
    ORANGE_DARK(92, 43, 1),

    PURPLE_LIGHT(126, 74, 236),
    PURPLE(104, 38, 245),
    PURPLE_MIDDLE(57, 10, 158),
    PURPLE_DARK(30, 10, 74),

    PINK_LIGHT(240, 98, 192),
    PINK(237, 31, 167),
    PINK_MIDDLE(136, 10, 93),
    PINK_DARK(83, 15, 60),

    CIAN_LIGHT(24, 212, 212),
    CIAN(3, 188, 188),
    CIAN_MIDDLE(6, 95, 95),
    CIAN_DARK(8, 63, 63),

    NEUTRAL_LIGHT(168, 168, 168),
    NEUTRAL(112, 112, 112),
    NEUTRAL_MIDDLE(75, 75, 75),
    NEUTRAL_DARK(54, 54, 54),
    ;

    override fun toRGB() = toRGB(red, green, blue)

}