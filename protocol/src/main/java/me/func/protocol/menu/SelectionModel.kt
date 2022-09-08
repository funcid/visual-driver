package me.func.protocol.menu

open class SelectionModel(
    open val storage: MutableList<out Button>,
    override var rows: Int,
    override var columns: Int,
): Page {
    open var title: String = ""
    open var vault: String = "coin"
    open var hint: String = ""
    open var money: String = ""

    constructor() : this(arrayListOf<Button>(), 3, 4)
}