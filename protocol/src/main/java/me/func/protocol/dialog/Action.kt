package me.func.protocol.dialog

data class Action(var type: String) {

    constructor(actions: Actions) : this(actions.name.lowercase())

    var screen: Screen? = null
    var command: String? = null

    fun screen(screen: Screen): Action {
        this.screen = screen

        return this
    }

    @JvmName("command1")
    fun command(command: String): Action {
        this.command = command
        return this
    }

    companion object {
        @JvmStatic
        fun command(command: String): Action {
            return Action(Actions.COMMAND).command(command)
        }
    }
}