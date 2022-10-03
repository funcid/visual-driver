package me.func.protocol.data.element

import me.func.protocol.Unique
import java.util.*

@Suppress("UNCHECKED_CAST")
open class Banner(
    override var uuid: UUID = UUID.randomUUID(),
    var motionType: MotionType = MotionType.CONSTANT,
    var watchingOnPlayer: Boolean = false,
    var watchingOnPlayerWithoutPitch: Boolean = false,
    var motionSettings: MutableMap<String, Any> = mutableMapOf(
        "yaw" to 0.0,
        "pitch" to 0.0,
        "xray" to true
    ),
    open var content: String = "",
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var height: Int = 100,
    var width: Int = 100,
    var texture: String = "",
    var red: Int = 0,
    var green: Int = 0,
    var blue: Int = 0,
    var opacity: Double = 0.62
) : Unique {

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(val banner: Banner = Banner()) {
        fun motionType(motionType: MotionType) = apply { banner.motionType = motionType }
        fun watchingOnPlayer(watchingOnPlayer: Boolean) = apply { banner.watchingOnPlayer = watchingOnPlayer }
        fun watchingOnPlayerWithoutPitch(watchingOnPlayerWithoutPitch: Boolean) =
            apply { banner.watchingOnPlayerWithoutPitch = watchingOnPlayerWithoutPitch }

        fun content(vararg content: String) = apply { banner.content = content.joinToString("\n") }
        fun resizeLine(lineIndex: Int, textScale: Double): Builder {
            val list = banner.motionSettings.computeIfAbsent("line") {
                mutableListOf<Pair<Int, Double>>()
            } as MutableList<Pair<Int, Double>>
            list.add(lineIndex to textScale)
            return this
        }

        fun x(x: Double) = apply { banner.x = x }
        fun y(y: Double) = apply { banner.y = y }
        fun z(z: Double) = apply { banner.z = z }
        fun yaw(yaw: Float) = apply { banner.motionSettings["yaw"] = yaw }
        fun pitch(pitch: Float) = apply { banner.motionSettings["pitch"] = pitch }
        fun height(height: Int) = apply { banner.height = height }
        fun width(width: Int) = apply { banner.width = width }
        fun texture(texture: String) = apply { banner.texture = texture }
        fun red(red: Int) = apply { banner.red = red }
        fun green(green: Int) = apply { banner.green = green }
        fun blue(blue: Int) = apply { banner.blue = blue }
        fun opacity(opacity: Double) = apply { banner.opacity = opacity }

        fun build() = banner
    }
}
