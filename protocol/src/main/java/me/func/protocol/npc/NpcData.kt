package me.func.protocol.npc

import java.util.*

data class NpcData(
    val uuid: UUID,
    var id: Int,
    var type: Int,
    var name: String? = null,
    var behaviour: NpcBehaviour? = null,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var pitch: Float = 0f,
    var yaw: Float = 0f,
    var skinUrl: String? = null,
    var skinDigest: String? = null,
    var slimArms: Boolean = false,
)