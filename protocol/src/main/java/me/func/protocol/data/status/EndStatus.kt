package me.func.protocol.data.status

enum class EndStatus(
    val title: String,
    val red: Int,
    val green: Int,
    val blue: Int,
    val offset: Double
) {
    WIN("Победа!", 35, 170, 65, -60.7),
    LOSE("Поражение", 185, 25, 25,  -60.7),
    DRAW("Ничья", 200, 119, 42,  -55.7)
}
