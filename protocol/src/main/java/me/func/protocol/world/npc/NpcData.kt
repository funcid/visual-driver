package me.func.protocol.world.npc

import java.util.UUID

class NpcData(
    var id: Int = (Math.random() * Int.MAX_VALUE).toInt(),
    var uuid: UUID = UUID.randomUUID(),
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
    var skinValue: String? = null,
    var skinSignature: String? = null,
    var slimArms: Boolean = false,
    var sneaking: Boolean = false,
    var sleeping: Boolean = false,
    var sitting: Boolean = false,
    var activationDistance: Int = -1,
)  {
    constructor(init: NpcData.() -> Unit) : this() { this.init() }

    companion object {

        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private val npcData: NpcData = NpcData()

        fun id(id: Int) = apply { npcData.id = id }
        fun uuid(uuid: UUID) = apply { npcData.uuid = uuid }
        fun x(x: Double) = apply { npcData.x = x }
        fun y(y: Double) = apply { npcData.y = y }
        fun z(z: Double) = apply { npcData.z = z }
        fun type(type: Int) = apply { npcData.type = type }
        fun name(name: String) = apply { npcData.name = name }
        fun behaviour(behaviour: NpcBehaviour) = apply { npcData.behaviour = behaviour }
        fun yaw(yaw: Float) = apply { npcData.yaw = yaw }
        fun pitch(pitch: Float) = apply { npcData.pitch = pitch }
        fun skinUrl(skinUrl: String) = apply { npcData.skinUrl = skinUrl }
        fun skinDigest(skinDigest: String) = apply { npcData.skinDigest = skinDigest }
        fun skinValue(skinValue: String) = apply { npcData.skinValue = skinValue }
        fun skinSignature(skinSignature: String) = apply { npcData.skinSignature = skinSignature }
        fun slimArms(slimArms: Boolean) = apply { npcData.slimArms = slimArms }
        fun sneaking(sneaking: Boolean) = apply { npcData.sneaking = sneaking }
        fun sleeping(sleeping: Boolean) = apply { npcData.sleeping = sleeping }
        fun sitting(sitting: Boolean) = apply { npcData.sitting = sitting }
        fun activationDistance(activationDistance: Int) = apply { npcData.activationDistance = activationDistance }

        fun build() = npcData
    }
}