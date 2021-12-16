package me.func.protocol

enum class DropRare(val title: String, val color: String) {

    COMMON("Обычный", "§a"),
    RARE("Редкий", "§9"),
    EPIC("Эпический", "§5"),
    LEGENDARY("Легендарный", "§6"),;

    fun with(content: String): String {
        return "${getColored()} §7$content"
    }

    fun getColored(): String {
        return "$color$title"
    }
}