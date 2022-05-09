package gui

import dev.xdark.clientapi.resource.ResourceLocation
import java.util.*
import kotlin.math.floor

class Storage(
    var uuid: UUID,
    var title: String,
    var money: String,
    var hint: String,
    var rows: Int,
    var columns: Int,
    var storage: MutableList<StoragePosition>,
    var page: Int = 1
) {
    private fun getPageSize() = rows * columns

    fun getPagesCount() = floor(storage.size * 1.0 / getPageSize())

    fun getElementsOnPage(pageIndex: Int) = storage.drop(getPageSize() * pageIndex).take(getPageSize())
}

class StoragePosition(
    var texture: ResourceLocation,
    var price: Int,
    var title: String,
    var description: String
)