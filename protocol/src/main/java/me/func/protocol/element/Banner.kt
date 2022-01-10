package me.func.protocol.element

import java.util.*

class Banner(
    val uuid: UUID = UUID.randomUUID(),
    var motionType: MotionType = MotionType.CONSTANT,
    var watchingOnPlayer: Boolean = false,
    var motionSettings: MutableMap<String, Any> = mutableMapOf(
        "yaw" to 0.0,
        "pitch" to 0.0
    ),
    var content: String = "",
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var height: Int = 100,
    var weight: Int = 100,
    var texture: String = "",
    var red: Int = 0,
    var green: Int = 0,
    var blue: Int = 0,
    var opacity: Double = 0.62
) {
    constructor(init: Banner.() -> Unit) : this() { this.init() }
}