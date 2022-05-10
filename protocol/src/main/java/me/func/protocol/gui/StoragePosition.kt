package me.func.protocol.gui

import java.util.*
import kotlin.math.floor

open class Storage(
    var uuid: UUID,
    var title: String,
    var money: String,
    var hint: String,
    var rows: Int,
    var columns: Int,
    var storage: List<StoragePosition> = listOf(),
    var page: Int = 1
) {
    private fun getPageSize() = rows * columns

    fun getPagesCount() = floor(storage.size * 1.0 / getPageSize())

    fun getElementsOnPage(pageIndex: Int) = storage.drop(getPageSize() * pageIndex).take(getPageSize())
}

open class StoragePosition(
    var texture: String,
    var price: Int,
    var title: String,
    var description: String
)