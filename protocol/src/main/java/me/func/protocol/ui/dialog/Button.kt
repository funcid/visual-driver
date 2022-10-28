package me.func.protocol.ui.dialog

class Button(var text: String) {

    var actions: List<Action>? = null

    fun actions(vararg actions: Action): Button {
        this.actions = actions.toList()
        return this
    }
}