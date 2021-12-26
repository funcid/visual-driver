package me.func.protocol.npc

import java.util.*

data class NpcData(
    var id: Int = (Math.random() * Int.MAX_VALUE).toInt(),
    val uuid: UUID = UUID.randomUUID(),
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var type: Int = 1000,
    var name: String? = null,
    var behaviour: NpcBehaviour = NpcBehaviour.NONE,
    var pitch: Float = 0f,
    var yaw: Float = 0f,
    var skinUrl: String? = null,
    var skinDigest: String? = null,
    var slimArms: Boolean = false,
    var sneaking: Boolean = false,
    var sleeping: Boolean = false,
    val sitting: Boolean = false
) {
    constructor(init: NpcData.() -> Unit) : this() { this.init() }
}