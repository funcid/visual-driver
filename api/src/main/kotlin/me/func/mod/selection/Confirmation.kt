package me.func.mod.selection

import me.func.mod.selection.MenuManager.bind
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer

class Confirmation(override var uuid: UUID = UUID.randomUUID(), var text: String, var onAccept: Consumer<Player>) :
    Openable {

    constructor(vararg text: String, accept: Consumer<Player>) : this(text.toList(), accept)

    constructor(text: List<String>, accept: Consumer<Player>) : this(UUID.randomUUID(), text.joinToString("\n"), accept)

    override fun open(player: Player) = apply { bind(player).string(text).send("func:accept", player) }
}