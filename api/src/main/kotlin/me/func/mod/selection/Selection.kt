package me.func.mod.selection

import me.func.mod.selection.MenuManager.open
import org.bukkit.entity.Player
import java.util.UUID

class Selection(
    override var uuid: UUID = UUID.randomUUID(),
    override var title: String = "Меню",
    var money: String = "",
    var vault: String = "coin",
    var hint: String = "Купить",
    var rows: Int = 3,
    var columns: Int = 4,
    override var storage: MutableList<Button> = mutableListOf()
) : Storage {
    constructor(title: String, money: String, hint: String, rows: Int, columns: Int, vararg storage: Button) :
            this(UUID.randomUUID(), title, money, "coin", hint, rows, columns, storage.toMutableList())

    override fun open(player: Player) = open(player, "storage:open") {
        string(vault).string(money).string(hint).integer(rows).integer(columns)
    }
}