package me.func.protocol

enum class EndStatus(
    val title: String,
    val red: Int,
    val green: Int,
    val blue: Int,
    val texture: String,
    val offset: Double
) {
    WIN("Победа!", 35, 170, 65, "mF7DWoV.png", 60.7),
    LOSE("Поражение", 185, 25, 25, "vxyu2tZ.png", 60.7),
    NOONE("Ничья", 200, 119, 42, "zUxhQ7y.png", 55.7)
}