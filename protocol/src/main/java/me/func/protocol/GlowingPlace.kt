package me.func.protocol

import java.util.*

class GlowingPlace(
    val uuid: UUID = UUID.randomUUID(),
    val rgb: RGB,
    val x: Double,
    val y: Double,
    val z: Double,
    val radius: Double = 1.3,
    val angles: Int = 12
) {
    constructor(color: RGB, x: Double, y: Double, z: Double, radius: Double = 1.3) :
            this(UUID.randomUUID(), color, x, y, z, radius, 12)

    constructor(color: RGB, x: Double, y: Double, z: Double) :
            this(UUID.randomUUID(), color, x, y, z, 1.3, 12)
}
