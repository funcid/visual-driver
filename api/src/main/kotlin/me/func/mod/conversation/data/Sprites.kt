package me.func.mod.conversation.data

enum class Sprites {

    SOLO,
    DUO,
    SPECIAL,
    TRIO,
    TEAM,
    SQUAD,;

    fun path() = "download:${this.name.lowercase()}"

}