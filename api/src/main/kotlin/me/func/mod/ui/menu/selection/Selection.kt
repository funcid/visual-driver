package me.func.mod.ui.menu.selection

import me.func.mod.reactive.ReactiveButton
import me.func.mod.ui.menu.MenuManager
import me.func.mod.ui.menu.MenuManager.bind
import me.func.mod.ui.menu.Paginated
import me.func.mod.ui.menu.Storage
import me.func.protocol.ui.menu.SelectionModel
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer

open class Selection(
    override var uuid: UUID = UUID.randomUUID(),
    override var title: String = "Меню",
    override var money: String = "",
    override var vault: String = "\uE03C",
    override var hint: String = "Купить",
    override var rows: Int = 3,
    override var columns: Int = 4,
    override var storage: MutableList<ReactiveButton> = arrayListOf()
) : Paginated, SelectionModel(storage, rows, columns) {

    var tick: Consumer<Storage>? = null

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
        fun columns(columns: Int) = apply { selection.columns = columns }
        fun uuid(uuid: UUID) = apply { selection.uuid = uuid }
        fun storage(data: MutableList<ReactiveButton>) = apply { selection.storage.addAll(data) }
        fun storage(vararg data: ReactiveButton) = apply { selection.storage.addAll(data) }
        fun vault(vault: String) = apply { selection.vault = vault }
        fun build() = selection
    }

    override fun open(player: Player): Paginated {
        val selection = MenuManager.push(player, this)

        // Отправляем данные о меню
        bind(player)
            .string(info)
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
