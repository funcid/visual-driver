package me.func.mod.reactive

import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.protocol.data.color.RGB
import me.func.protocol.data.color.Tricolor
import me.func.protocol.data.element.MotionType
import me.func.protocol.math.Dimension
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.sqrt

class ReactiveBanner : PlayerSubscriber {

    override val isConstant = false
    override var uuid: UUID = UUID.randomUUID()
    val subscribed = arrayListOf<UUID>()

    override fun getSubscribersCount() = subscribed.size

    override fun removeSubscriber(player: Player) {
        subscribed.remove(player.uniqueId)
    }

    var content: String = ""
        set(value) {
            update(transfer().integer(1).string(value))
            field = value
        }

    var x: Double = 0.0
        set(value) {
            update(transfer().integer(2).integer(1).double(value))
            field = value
        }

    var y: Double = 0.0
        set(value) {
            update(transfer().integer(2).integer(2).double(value))
            field = value
        }

    var z: Double = 0.0
        set(value) {
            update(transfer().integer(2).integer(3).double(value))
            field = value
        }

    var height: Int = 100
        set(value) {
            update(transfer().integer(3).double(value.toDouble()))
            field = value
        }

    var weight: Int = 100
        set(value) {
            update(transfer().integer(4).double(value.toDouble()))
            field = value
        }

    var texture: String = ""
        set(value) {
            update(transfer().integer(5).string(value))
            field = value
        }

    var color: RGB = Tricolor(0, 0, 0)
        set(value) {
            update(transfer().integer(6).rgb(value))
            field = value
        }

    var opacity: Double = 0.62
        set(value) {
            update(transfer().integer(7).double(value))
            field = value
        }

    private fun transfer() = ModTransfer().uuid(uuid)

    private fun update(transfer: ModTransfer) = transfer.send(
        "banner:reactive-update",
        subscribed.mapNotNull(Bukkit::getPlayer)
    )

    var motionType: MotionType = MotionType.CONSTANT
    var watchingOnPlayer: Boolean = false
    var watchingOnPlayerWithoutPitch: Boolean = false
    var motionSettings: MutableMap<String, Any> = hashMapOf(
        "yaw" to 0.0,
        "pitch" to 0.0,
        "xray" to false
    )

    var carveSize: Double = 2.0
    var active: Boolean = true

    fun location(location: Location) {
        x = location.x
        y = location.y
        z = location.z
        motionSettings["yaw"] = location.yaw
        motionSettings["pitch"] = location.pitch
    }

    fun shineBlocks(boolean: Boolean) {
        motionSettings["xray"] = boolean
    }

    fun harmonicVibrations(dimension: Dimension, amplitude: Double, speed: Double, offset: Double) {
        motionType = MotionType.PERIODIC
        motionSettings["amplitude${dimension.name}"] = amplitude
        motionSettings["speed${dimension.name}"] = speed
        motionSettings["offset${dimension.name}"] = offset
    }

    fun target(entity: LivingEntity, offsetX: Double = 0.0, offsetY: Double = 0.0, offsetZ: Double = 0.0) {
        motionSettings["target"] = entity.entityId
        motionSettings["offsetX"] = offsetX
        motionSettings["offsetY"] = offsetY
        motionSettings["offsetZ"] = offsetZ
    }

    fun eyeLocation(location: Location) {
        motionSettings["yaw"] = Math.toDegrees(-kotlin.math.atan2(location.x - x, location.z - z)).toFloat()
        motionSettings["pitch"] = Math.toDegrees(
            kotlin.math.atan2(
                location.y - y,
                sqrt(StrictMath.pow(location.x - x, 2.0) + StrictMath.pow(location.z - z, 2.0))
            )
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(private val banner: ReactiveBanner = ReactiveBanner()) {
        fun motionType(motionType: MotionType) = apply { banner.motionType = motionType }
        fun watchingOnPlayer(watchingOnPlayer: Boolean) = apply { banner.watchingOnPlayer = watchingOnPlayer }
        fun watchingOnPlayerWithoutPitch(watchingOnPlayerWithoutPitch: Boolean) =
            apply { banner.watchingOnPlayerWithoutPitch = watchingOnPlayerWithoutPitch }

        fun content(vararg content: String) = apply { banner.content = content.joinToString("\n") }
        fun resizeLine(lineIndex: Int, textScale: Double): Builder {
            val list =
                (banner.motionSettings["line"] ?: mutableListOf<Pair<Int, Double>>()) as MutableList<Pair<Int, Double>>
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
        fun color(rgb: RGB) = apply { banner.color = rgb }
        fun red(red: Int) = apply { banner.color.red = red }
        fun green(green: Int) = apply { banner.color.green = green }
        fun blue(blue: Int) = apply { banner.color.blue = blue }
        fun opacity(opacity: Double) = apply { banner.opacity = opacity }
        fun carveSize(carveSize: Double) = apply { banner.carveSize = carveSize }
        fun active(active: Boolean) = apply { banner.active = active }

        fun build() = banner
    }
}