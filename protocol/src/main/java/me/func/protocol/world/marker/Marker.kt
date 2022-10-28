package me.func.protocol.world.marker

import java.util.UUID

class Marker(
    val uuid: UUID = UUID.randomUUID(),
    var x: Double,
    var y: Double,
    var z: Double,
    val scale: Double = 16.0,
    val texture: String
) {
    constructor(x: Double, y: Double, z: Double, scale: Double, type: MarkerSign) :
            this(UUID.randomUUID(), x, y, z, scale, type.texture)

    constructor(x: Double, y: Double, z: Double, type: MarkerSign) :
            this(UUID.randomUUID(), x, y, z, 16.0, type.texture)

    constructor(x: Double, y: Double, z: Double, scale: Double, texture: String) :
            this(UUID.randomUUID(), x, y, z, scale, texture)
}
