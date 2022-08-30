package me.func.protocol.dialog

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

    @JvmName("custom")
    fun custom(custom: Runnable) {
        this.custom = custom
        command("anime:dialog-callback " + UUID.randomUUID())
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