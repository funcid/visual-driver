package me.func.protocol.dialog

class Entrypoint(val id: String, val title: String, var screen: Screen) {

    var subtitle: String? = null

    fun subtitle(subtitle: String?): Entrypoint {
        this.subtitle = subtitle
        return this
    }
}