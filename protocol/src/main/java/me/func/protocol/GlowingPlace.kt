package me.func.protocol

import java.util.*

class GlowingPlace(
    val uuid: UUID = UUID.randomUUID(),
    val red: Int,
    val blue: Int,
    val green: Int,
    val x: Double,
    val y: Double,
    val z: Double,
    val radius: Double = 1.3,
    val angles: Int = 12
) {
    constructor(uuid: UUID = UUID.randomUUID(), color: GlowColor, x: Double, y: Double, z: Double, radius: Double = 1.3, angles: Int = 12) :
            this(uuid, color.red, color.green, color.blue, x, y, z, radius, angles)

    constructor(color: GlowColor, x: Double, y: Double, z: Double, radius: Double = 1.3) :
            this(UUID.randomUUID(), color.red, color.green, color.blue, x, y, z, radius, 12)

    constructor(color: GlowColor, x: Double, y: Double, z: Double) :
            this(UUID.randomUUID(), color.red, color.green, color.blue, x, y, z, 1.3, 12)
}
