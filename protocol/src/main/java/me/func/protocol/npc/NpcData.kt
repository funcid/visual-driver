package me.func.protocol.npc

data class NpcData(
    val id: Int,
    val type: Int,
    val name: String? = null,
    val behaviour: NpcBehaviour? = null,
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
    val pitch: Float = 0f,
    val yaw: Float = 0f,
    val skinUrl: String? = null,
    val skinDigest: String? = null,
    val slimArms: Boolean = false,
)