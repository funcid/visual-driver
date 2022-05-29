package me.func.mod.selection.queue

import me.func.mod.conversation.ModTransfer
import me.func.mod.selection.Openable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer

class QueueView(
    override var uuid: UUID,
    var title: String,
    var icon: String,
    var description: String,
) : Openable {
    constructor(title: String, icon: String, description: String) :
            this(UUID.randomUUID(), title, icon, description)

    constructor() : this("Поле title", "texture", "Поле description")

    var onLeave: Consumer<Player> = Consumer { }

    override fun open(player: Player) = apply {
        QueueViewer.views[player.uniqueId] = this
        ModTransfer()
            .string(icon)
            .string(title)
            .string(description)
            .send("queue:init", player)
    }

    fun update(description: String) = apply {
        val transfer = ModTransfer().string(description)
        Bukkit.getOnlinePlayers().filter { QueueViewer.views[it.uniqueId] == this }.forEach {
            transfer.send("queue:update", it)
        }
    }

    fun update(player: Player, description: String) = apply {
        ModTransfer().string(description).send("queue:update", player)
    }

    fun close(player: Player) = QueueViewer.stop(player)

    fun onLeave(consumer: Consumer<Player>) {
        onLeave = consumer
    }
}