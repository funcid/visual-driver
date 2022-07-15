package me.func.mod.selection

import me.func.mod.selection.MenuManager.open
import org.bukkit.entity.Player
import java.util.UUID

open class Selection(
    override var uuid: UUID = UUID.randomUUID(),
    override var title: String = "Меню",
    open var money: String = "",
    open var vault: String = "coin",
    open var hint: String = "Купить",
    open var rows: Int = 3,
    open var columns: Int = 4,
    override var storage: MutableList<Button> = mutableListOf()
) : Storage {
    constructor(title: String, money: String, hint: String, rows: Int, columns: Int, vararg storage: Button) :
            this(UUID.randomUUID(), title, money, "coin", hint, rows, columns, storage.toMutableList())

    constructor(
        title: String,
        money: String,
        vault: String,
        hint: String,
        rows: Int,
        columns: Int,
        storage: List<Button>
    ) : this(
        uuid = UUID.randomUUID(),
        title = title,
        money = money,
        vault = vault,
        hint = hint,
        rows = rows,
        columns = columns,
        storage = storage.toMutableList()
    )

    override fun open(player: Player) = open(player, "storage:open") {
        string(vault).string(money).string(hint).integer(rows).integer(columns)
    }
}
