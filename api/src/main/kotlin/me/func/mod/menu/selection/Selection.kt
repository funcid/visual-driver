package me.func.mod.menu.selection

import me.func.mod.menu.ReactiveButton
import me.func.mod.menu.MenuManager
import me.func.mod.menu.MenuManager.bind
import me.func.mod.menu.Paginated
import me.func.mod.menu.Storage
import me.func.protocol.menu.SelectionModel
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer

open class Selection(
    override var uuid: UUID = UUID.randomUUID(),
    override var title: String = "Меню",
    override var money: String = "",
    override var vault: String = "coin",
    override var hint: String = "Купить",
    override var rows: Int = 3,
    override var columns: Int = 4,
    override var storage: MutableList<ReactiveButton> = mutableListOf()
) : Paginated, SelectionModel(storage, rows, columns) {

    var tick: Consumer<Storage>? = null

    constructor(title: String, money: String, hint: String, rows: Int, columns: Int, vararg storage: ReactiveButton) :
            this(UUID.randomUUID(), title, money, "coin", hint, rows, columns, storage.toMutableList())

    constructor(
        title: String,
        money: String,
        vault: String,
        hint: String,
        rows: Int,
        columns: Int,
        storage: List<ReactiveButton>
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

    companion object {

        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private val selection: Selection = Selection()

        fun title(title: String) = apply { selection.title = title }
        fun money(money: String) = apply { selection.money = money }
        fun hint(hint: String) = apply { selection.hint = hint }
        fun rows(rows: Int) = apply { selection.rows = rows }
        fun uuid(uuid: UUID) = apply { selection.uuid = uuid }
        fun storage(storage: MutableList<ReactiveButton>) = apply { selection.storage = storage }
        fun storage(vararg storage: ReactiveButton) = apply { selection.storage = storage.toMutableList() }
        fun vault(vault: String) = apply { selection.vault = vault }
        fun build() = selection
    }

    override fun open(player: Player): Paginated {
        val selection = MenuManager.push(player, this)

        // Отправляем данные о меню
        bind(player)
            .string(title)
            .string(vault)
            .string(money)
            .string(hint)
            .integer(rows)
            .integer(columns)
            .integer(getPageCount())
            .send("storage:open", player)

        // Отправляем первую страницу
        selection.sendPage(0, player)

        return selection
    }
}
