package me.func.protocol

//import kotlinx.serialization.Serializable

// @Serializable TODO: Rewrite graffiti-service
enum class DropRare(val title: String, val color: String, val red: Int, val green: Int, val blue: Int) {

    COMMON("Обычный", "§a", 170, 170, 170),
    RARE("Редкий", "§9", 85, 85, 255),
    EPIC("Эпический", "§5", 170, 0, 170),
    LEGENDARY("Легендарный", "§6", 255, 170, 0), ;

    fun with(content: String): String {
        return "${getColored()} §7$content"
    }

    fun getColored(): String {
        return "$color$title"
    }
}