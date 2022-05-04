package me.func.protocol.element

import java.util.*

class Figure(
    val uuid: UUID = UUID.randomUUID(),
    val type: FigureType,
    var x: Double,
    var y: Double,
    var z: Double,
    var size: Double,
    var red: Int,
    var green: Int,
    var blue: Int,
    var alpha: Double,
    var texture: String?
)
