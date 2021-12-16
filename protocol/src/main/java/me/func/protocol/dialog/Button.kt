package me.func.protocol.dialog

data class Button(var text: String) {

    var actions: List<me.func.protocol.dialog.Action>? = null

    fun actions(vararg actions: me.func.protocol.dialog.Action): Button {
        this.actions = actions.toList()
        return this
    }
}