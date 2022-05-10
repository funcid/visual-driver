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
    var texture: String,
    var price: Long,
    var title: String,
    var description: String
)