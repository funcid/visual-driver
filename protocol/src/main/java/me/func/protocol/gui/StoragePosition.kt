package me.func.protocol.gui

import java.util.*

class Storage(
    var uuid: UUID,
    var title: String,
    var money: String,
    var hint: String,
    var rows: Int,
    var columns: Int,
    var storage: List<StoragePosition> = listOf(),
)

open class StoragePosition(
    open var texture: String? = null,
    var price: Long = -1,
    open var title: String? = null,
    open var description: String? = null
)