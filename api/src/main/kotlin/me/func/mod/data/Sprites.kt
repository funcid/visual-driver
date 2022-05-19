package me.func.mod.data

enum class Sprites {

    SOLO,
    DUO,
    SPECIAL,
    TRIO,
    SQUAD,;

    fun path() = "download:${this.name.lowercase()}"

}