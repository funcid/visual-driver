package me.func.mod.selection

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer

class Reconnect(var text: String, var secondsLeft: Int, var hint: String, var onClick: Consumer<Player>) :
    Openable {

    override var uuid: UUID = UUID.randomUUID()

    constructor(secondsLeft: Int, consumer: Consumer<Player>) : this("Вернуться в игру", secondsLeft, "Войти", consumer)

    constructor(consumer: Consumer<Player>) : this(180, consumer)

    override fun open(player: Player) =
        apply {
            MenuManager.reconnectMap[player.uniqueId] = this@Reconnect
            ModTransfer()
                .integer(secondsLeft)
                .string(text)
                .string(hint)
                .send("func:reconnect", player)
        }
}