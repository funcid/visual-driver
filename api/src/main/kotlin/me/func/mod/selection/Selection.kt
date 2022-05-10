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
    constructor(title: String, money: String, hint: String, rows: Int, columns: Int, vararg storage: Button) :
            this(UUID.randomUUID(), title, money, hint, rows, columns, storage.toList())

    fun buttons(vararg setup: Button) = apply { storage = setup.toList() }

    fun open(player: Player) = apply {
        SelectionManager.handleMap[player.uniqueId] = this
        ModTransfer()
            .string(uuid.toString())
            .string(title)
            .string(money)
            .string(hint)
            .integer(rows)
            .integer(columns)
            .integer(storage?.size ?: 0)
            .apply {
                storage?.forEach {
                    string(it.texture)
                        .long(it.price)
                        .string(it.title)
                        .string(it.description)
                }
            }.send("storage:open", player)
    }
}