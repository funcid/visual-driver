package me.func.protocol.menu

import com.google.gson.annotations.SerializedName

open class SelectionModel(
    @SerializedName("storage")
    open var data: MutableList<out Button> = arrayListOf(),
    override var rows: Int = 3,
    override var columns: Int = 4,
): Page {

    open var title: String = ""
    open var vault: String = "\uE03C"
    open var hint: String = ""
    open var money: String = ""

    fun pageSize() = rows * columns

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(val model: SelectionModel = SelectionModel(arrayListOf(), 4, 3)) {
        fun storage(vararg data: Button) = apply { model.data = data.toMutableList() }
        fun storage(data: Iterable<Button>) = apply { model.data = data.toMutableList() }
        fun title(title: String) = apply { model.title = title }
        fun vault(vault: String) = apply { model.vault = vault }
        fun hint(hint: String) = apply { model.hint = hint }
        fun money(money: String) = apply { model.money = money }
        fun rows(rows: Int) = apply { model.rows = rows }
        fun columns(columns: Int) = apply { model.columns = columns }
        fun build() = model
    }
}