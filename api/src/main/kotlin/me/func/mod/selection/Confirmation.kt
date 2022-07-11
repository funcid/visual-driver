package me.func.mod.selection

import me.func.mod.selection.MenuManager.bind
import org.bukkit.entity.Player
import java.util.UUID
import java.util.function.Consumer

class Confirmation @JvmOverloads constructor(
    override var uuid: UUID = UUID.randomUUID(),
    var text: String,
    var onAccept: Consumer<Player>,
    var onDeny: Consumer<Player>? = null,
) : Openable {
    @JvmOverloads
    constructor(
        vararg text: String,
        accept: Consumer<Player>,
        deny: Consumer<Player>? = null
    ) : this(
        text.toList(),
        accept,
        deny
    )

    @JvmOverloads
    constructor(
        text: List<String>,
        accept: Consumer<Player>,
        deny: Consumer<Player>? = null
    ) : this(
        UUID.randomUUID(),
        text.joinToString("\n"),
        accept,
        deny
    )

    override fun open(player: Player) = apply { bind(player).string(text).send("func:accept", player) }
}
