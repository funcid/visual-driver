package me.func.mod.selection

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player
import java.util.*

class Selection(
    var uuid: UUID = UUID.randomUUID(),
    var title: String = "Меню",
    var money: String = "Загрузка...",
    var hint: String = "Купить",
    var rows: Int = 3,
    var columns: Int = 4,
    var storage: List<Button>? = null
) {
    fun open(player: Player) = apply {
        SelectionManager.handleMap[player.uniqueId] = this
        ModTransfer()
            .string(toString())
            .string(title)
            .string(money)
            .string(hint)
            .integer(rows)
            .integer(columns)
            .integer(storage?.size ?: 0)
            .apply {
                storage?.forEach {
                    string(it.texture)
                        .integer(it.price)
                        .string(it.title)
                        .string(it.description)
                }
            }.send("storage:open", player)
    }
}