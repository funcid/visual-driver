package me.func.protocol.personalization

import kotlinx.serialization.Serializable
import me.func.protocol.util.UUIDSerializer
import java.util.UUID

@Serializable
data class GraffitiPlaced(
    @Serializable(with = UUIDSerializer::class)
    var owner: UUID,
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
    var onGround: Boolean = false,
    var local: Boolean = false
)
