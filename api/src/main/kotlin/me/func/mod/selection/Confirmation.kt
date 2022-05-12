package me.func.mod.selection

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer

class Confirmation(override var uuid: UUID = UUID.randomUUID(), var text: String, var onAccept: Consumer<Player>) :
    Openable {

    constructor(vararg text: String, accept: Consumer<Player>) : this(
        UUID.randomUUID(),
        text.joinToString("\n"),
        accept
    )

    override fun open(player: Player) = apply {
        ModTransfer()
            .string(uuid.toString())
            .string(text)
            .send("func:accept", player)
    }
}