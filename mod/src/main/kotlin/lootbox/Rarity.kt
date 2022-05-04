package lootbox

import ru.cristalix.uiengine.utility.Color

val NOTHING = Rarity("Не повезло!", Color(0xD2, 0x37, 0x44, 1.0))
val COMMON = Rarity("Обычный предмет", Color(0xB4, 0xB4, 0xB4, 1.0))
val UNCOMMON = Rarity("Необычный предмет", Color(0x22, 0xAE, 0x49, 1.0))
val RARE = Rarity("Редкий предмет", Color(0x2B, 0x74, 0xDF, 1.0))
val EPIC = Rarity("Эпический предмет", Color(0x80, 0x3C, 0xEE, 1.0))
val LEGENDARY = Rarity("Легендарный предмет", Color(0xE2, 0x84, 0x2C, 1.0))
val INCREDIBLE = Rarity("НЕВЕРОЯТНЫЙ ПРЕДМЕТ", Color(0xD2, 0x37, 0x44, 1.0))

class Rarity(
    val name: String,
    val color: Color
)