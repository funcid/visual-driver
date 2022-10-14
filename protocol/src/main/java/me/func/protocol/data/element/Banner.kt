package me.func.protocol.data.element

import java.util.UUID

class Banner(
    var uuid: UUID = UUID.randomUUID(),
    var motionType: MotionType = MotionType.CONSTANT,
    var watchingOnPlayer: Boolean = false,
    var watchingOnPlayerWithoutPitch: Boolean = false,
    var motionSettings: MutableMap<String, Any> = hashMapOf(
        "yaw" to 0.0,
        "pitch" to 0.0,
        "xray" to true
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
    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    constructor(init: Banner.() -> Unit) : this() { this.init() }

    class Builder(val banner: Banner = Banner()) {
        fun motionType(motionType: MotionType) = apply { banner.motionType = motionType }
        fun watchingOnPlayer(watchingOnPlayer: Boolean) = apply { banner.watchingOnPlayer = watchingOnPlayer }
        fun watchingOnPlayerWithoutPitch(watchingOnPlayerWithoutPitch: Boolean) = apply { banner.watchingOnPlayerWithoutPitch = watchingOnPlayerWithoutPitch }
        fun content(vararg content: String) = apply { banner.content = content.joinToString("\n") }
        fun resizeLine(lineIndex: Int, textScale: Double): Builder {
            val list = (banner.motionSettings["line"] ?: mutableListOf<Pair<Int, Double>>()) as MutableList<Pair<Int, Double>>
            list.add(lineIndex to textScale)
            banner.motionSettings["line"] = list
            return this
        }
        fun x(x: Double) = apply { banner.x = x }
        fun y(y: Double) = apply { banner.y = y }
        fun z(z: Double) = apply { banner.z = z }
        fun yaw(yaw: Float) = apply { banner.motionSettings["yaw"] = yaw }
        fun pitch(pitch: Float) = apply { banner.motionSettings["pitch"] = pitch }
        fun xray(xray: Double) = apply { banner.motionSettings["xray"] = xray }
        fun height(height: Int) = apply { banner.height = height }
        fun weight(weight: Int) = apply { banner.weight = weight }
        fun texture(texture: String) = apply { banner.texture = texture }
        fun red(red: Int) = apply { banner.red = red }
        fun green(green: Int) = apply { banner.green = green }
        fun blue(blue: Int) = apply { banner.blue = blue }
        fun opacity(opacity: Double) = apply { banner.opacity = opacity }

        fun build() = banner
    }
}
