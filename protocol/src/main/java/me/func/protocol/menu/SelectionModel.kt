package me.func.protocol.menu

import java.util.*

open class SelectionModel(
    open val storage: MutableList<out Button>,
    override val rows: Int,
    override val columns: Int,
    val localUuid: UUID = UUID.randomUUID()
): Page {
    open val title: String? = ""
    open val vault: String? = ""
    open val hint: String? = ""
    open val money: String? = ""
}