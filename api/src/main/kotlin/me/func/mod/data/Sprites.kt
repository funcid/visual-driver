package me.func.mod.data

enum class Sprites {

    SOLO,
    DUO,
    SPECIAL,
    TRIO,
    TEAM,
    SQUAD,;

    fun path() = "download:${this.name.lowercase()}"

}