package me.func.protocol.element

import java.util.*

data class Figure(
    val uuid: UUID = UUID.randomUUID(),
    val type: FigureType,
    var x: Double,
    var y: Double,
    var z: Double,
    var size: Double,
    var color: Int,
    var texture: String?
)
