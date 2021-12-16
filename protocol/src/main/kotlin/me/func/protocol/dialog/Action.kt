package me.func.protocol.dialog

data class Action(var type: String) {

    constructor(actions: me.func.protocol.dialog.Actions) : this(actions.name.toLowerCase())

    var screen: me.func.protocol.dialog.Screen? = null
    var command: String? = null

    fun screen(screen: me.func.protocol.dialog.Screen): me.func.protocol.dialog.Action {
        this.screen = screen

        return this
    }

    @JvmName("command1")
    fun command(command: String): me.func.protocol.dialog.Action {
        this.command = command
        return this
    }

    companion object {
        @JvmStatic
        fun command(command: String): me.func.protocol.dialog.Action {
            return me.func.protocol.dialog.Action(me.func.protocol.dialog.Actions.COMMAND).command(command)
        }
    }
}