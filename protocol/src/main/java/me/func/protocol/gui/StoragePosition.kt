package me.func.protocol.gui

import java.util.*
import kotlin.math.floor
import kotlin.math.round

class Storage(
    var uuid: UUID,
    var title: String,
    var money: String,
    var hint: String,
    var rows: Int,
    var columns: Int,
    var storage: List<StoragePosition> = listOf(),
    var page: Int = 0
) {
    fun getPageSize() = rows * columns

    fun getElementsOnPage(pageIndex: Int) = storage.drop(getPageSize() * pageIndex).take(getPageSize())
}

open class StoragePosition(
    var texture: String,
    var price: Int,
    var title: String,
    var description: String
)