package me.func.protocol.ui.dialog

import java.util.*

class Action(var type: String) {

    constructor(actions: Actions) : this(actions.name.lowercase())

    var screen: Screen? = null
    var command: String? = null
    var custom: Runnable? = null

    fun screen(screen: Screen?): Action {
        this.screen = screen
        return this
    }

    @JvmName("custom1")
    fun custom(custom: Runnable): Action {
        this.custom = custom
        return command("anime:dialog-callback " + UUID.randomUUID())
    }

    @JvmName("command1")
    fun command(command: String): Action {
        this.command = command
        return this
    }

    companion object {
        @JvmStatic
        fun command(command: String) = Action(Actions.COMMAND).command(command)

        @JvmStatic
        fun custom(custom: Runnable) = Action(Actions.COMMAND).custom(custom)
    }
}