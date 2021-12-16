package me.func.protocol

import java.util.*

data class Marker(
    val uuid: UUID = UUID.randomUUID(),
    var x: Double,
    var y: Double,
    var z: Double,
    val scale: Double = 16.0,
    val texture: String
) {
    constructor(x: Double, y: Double, z: Double, scale: Double, type: me.func.protocol.MarkerSign) :
            this(UUID.randomUUID(), x, y, z, scale, type.texture)

    constructor(x: Double, y: Double, z: Double, type: me.func.protocol.MarkerSign) :
            this(UUID.randomUUID(), x, y, z, 16.0, type.texture)

    constructor(x: Double, y: Double, z: Double, scale: Double, texture: String) :
            this(UUID.randomUUID(), x, y, z, scale, texture)
}
