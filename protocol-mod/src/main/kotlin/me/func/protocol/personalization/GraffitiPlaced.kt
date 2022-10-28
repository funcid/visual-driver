package me.func.protocol.personalization

import java.util.UUID

class GraffitiPlaced(
    var owner: UUID,
    var ownerName: String,
    val world: String,
    var graffiti: Graffiti,
    var x: Double,
    var y: Double,
    var z: Double,
    var ticksLeft: Int,
    var rotationAngle: Double = 0.0,
    var rotationAxisX: Double = 0.0,
    var rotationAxisY: Double = 0.0,
    var rotationAxisZ: Double = 0.0,
    var extraRotation: Double = 0.0,
    var onGround: Boolean = false
)
