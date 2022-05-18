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
    open var texture: String,
    var price: Long,
    open var title: String,
    open var description: String
)