package me.func.mod.reactive

import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.protocol.data.color.RGB
import me.func.protocol.data.element.Banner
import me.func.protocol.data.element.MotionType
import me.func.protocol.math.Dimension
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.sqrt

/**
 * Created by Daniil Sudomoin on 03.10.2022.
 **/
class ReactiveBanner : Banner(), PlayerSubscriber {

    override val isConstant: Boolean = false

    private val subscribed = hashSetOf<UUID>()

    override var content: String = super.content
        set(value) {
            if (field != value) {
                update(starter().integer(1).string(value))
                field = value
            }
        }

    override fun removeSubscriber(player: Player) {
        subscribed.remove(player.uniqueId)
    }

    override fun getSubscribersCount() = subscribed.size

    fun unsubscribe(vararg uuids: UUID) {
        subscribed.removeAll(uuids.toHashSet())
    }

    fun unsubscribe(vararg players: Player) {
        subscribed.removeAll(players.mapTo(hashSetOf()) { it.uniqueId })
    }

    fun send(players: Collection<Player>) = send(*players.toTypedArray())

    fun send(vararg players: Player) {
        subscribed.addAll(players.map { it.uniqueId })

        starter()
            .integer(motionType.ordinal)
            .boolean(watchingOnPlayer)
            .boolean(watchingOnPlayerWithoutPitch)
            .double(-motionSettings["yaw"].toString().toDouble())
            .double(motionSettings["pitch"].toString().toDouble())
            .string(content)
            .double(x)
            .double(y)
            .double(z)
            .integer(height)
            .integer(width)
            .string(texture)
            .integer(red)
            .integer(green)
            .integer(blue)
            .double(opacity)
            .apply {
                if (motionType == MotionType.STEP_BY_TARGET) {
                    integer(motionSettings["target"].toString().toInt())
                    double(motionSettings["offsetX"].toString().toDouble())
                    double(motionSettings["offsetY"].toString().toDouble())
                    double(motionSettings["offsetZ"].toString().toDouble())
                }
            }
            .send("banner:new", *players)

        val lines = motionSettings["line"]
        if (lines != null) {
            val sizes = lines as MutableList<Pair<Int, Double>>
            val sizeTransfer = starter().integer(sizes.size)
            sizes.forEach { sizeTransfer.integer(it.first).double(it.second) }
            sizeTransfer.send("banner:size-text", *players)
        }
    }

    @JvmOverloads
    fun delete(players: Set<UUID> = subscribed) {
        starter().send("banner:remove", players.mapNotNull { Bukkit.getPlayer(uuid) })
        unsubscribe(*players.toTypedArray())
    }

    fun delete(vararg players: Player) {
        delete(players.mapTo(hashSetOf()) { it.uniqueId })
    }

    fun delete(vararg uuids: UUID) {
        delete(uuids.toSet())
    }

    private fun update(transfer: ModTransfer) {
        subscribed.removeIf { Bukkit.getPlayer(it) == null }
        transfer.send("banner:update", subscribed.map { Bukkit.getPlayer(it) })
    }

    private fun starter() = ModTransfer().uuid(uuid)

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(val model: ReactiveBanner = ReactiveBanner()) {

        fun motionType(motionType: MotionType) = apply { model.motionType = motionType }
        fun watchingOnPlayer(watchingOnPlayer: Boolean) = apply { model.watchingOnPlayer = watchingOnPlayer }
        fun watchingOnPlayerWithoutPitch(watchingOnPlayerWithoutPitch: Boolean) =
            apply { model.watchingOnPlayerWithoutPitch = watchingOnPlayerWithoutPitch }

        fun content(vararg content: String) = apply { model.content = content.joinToString("\n") }

        fun resizeLine(lineIndex: Int, textScale: Double): Builder {
            val list = model.motionSettings.computeIfAbsent("line") {
                mutableListOf<Pair<Int, Double>>()
            } as MutableList<Pair<Int, Double>>
            list.add(lineIndex to textScale)
            return this
        }

        fun x(x: Double) = apply { model.x = x }
        fun y(y: Double) = apply { model.y = y }
        fun z(z: Double) = apply { model.z = z }
        fun yaw(yaw: Float) = apply { model.motionSettings["yaw"] = yaw }
        fun pitch(pitch: Float) = apply { model.motionSettings["pitch"] = pitch }
        fun location(location: Location) = apply {
            x(location.x)
            y(location.y)
            z(location.z)
            yaw(location.yaw)
            pitch(location.pitch)
        }

        fun height(height: Int) = apply { model.height = height }
        fun width(width: Int) = apply { model.width = width }
        fun texture(texture: String) = apply { model.texture = texture }
        fun red(red: Int) = apply { model.red = red }
        fun green(green: Int) = apply { model.green = green }
        fun blue(blue: Int) = apply { model.blue = blue }
        fun opacity(opacity: Double) = apply { model.opacity = opacity }
        fun color(rgb: RGB) = apply {
            red(rgb.red)
            green(rgb.green)
            blue(rgb.blue)
        }

        fun eyeLocation(location: Location) = apply {
            model.motionSettings["yaw"] =
                Math.toDegrees(-kotlin.math.atan2(location.x - model.x, location.z - model.z)).toFloat()
            model.motionSettings["pitch"] = Math.toDegrees(
                kotlin.math.atan2(
                    location.y - model.y,
                    sqrt(StrictMath.pow(location.x - model.x, 2.0) + StrictMath.pow(location.z - model.z, 2.0))
                )
            )
        }

        fun target(entity: LivingEntity, offsetX: Double = 0.0, offsetY: Double = 0.0, offsetZ: Double = 0.0) = apply {
            model.motionSettings["target"] = entity.entityId
            model.motionSettings["offsetX"] = offsetX
            model.motionSettings["offsetY"] = offsetY
            model.motionSettings["offsetZ"] = offsetZ
        }

        fun shineBlocks(boolean: Boolean) = apply {
            model.motionSettings["xray"] = boolean
        }

        fun harmonicVibrations(dimension: Dimension, amplitude: Double, speed: Double, offset: Double) = apply {
            model.motionType = MotionType.PERIODIC
            model.motionSettings["amplitude${dimension.name}"] = amplitude
            model.motionSettings["speed${dimension.name}"] = speed
            model.motionSettings["offset${dimension.name}"] = offset
        }

        fun build() = model
    }
}