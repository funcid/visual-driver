package me.func.mod

import me.func.mod.conversation.ModTransfer
import me.func.protocol.Dimension
import me.func.protocol.element.Banner
import me.func.protocol.element.MotionType
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.lang.StrictMath.pow
import java.util.*
import kotlin.math.sqrt

object Banners {

    val banners = mutableMapOf<UUID, Banner>()

    fun new(data: Banner.() -> Unit): Banner {
        val banner = Banner().apply(data)
        banners[banner.uuid] = banner
        return banner
    }

    @JvmStatic
    fun new(banner: Banner): Banner {
        banners[banner.uuid] = banner
        return banner
    }

    @JvmStatic
    fun content(player: Player, uuid: UUID, content: String) = ModTransfer(uuid.toString(), content).send("banner:change-content", player)

    @JvmStatic
    fun content(player: Player, banner: Banner, content: String) = content(player, banner.uuid, content)

    @JvmStatic
    fun show(player: Player, vararg uuid: UUID) = show(player, *uuid.mapNotNull { banners[it] }.toTypedArray())

    @JvmStatic
    fun show(player: Player, vararg banner: Banner) {
        val transfer = ModTransfer().integer(banner.size)

        for (current in banner) {
            transfer.string(current.uuid.toString())
                .integer(current.motionType.ordinal)
                .boolean(current.watchingOnPlayer)
                .double(-current.motionSettings["yaw"].toString().toDouble())
                .double(current.motionSettings["pitch"].toString().toDouble())
                .string(current.content)
                .double(current.x)
                .double(current.y)
                .double(current.z)
                .integer(current.weight)
                .integer(current.height)
                .string(current.texture)
                .integer(current.red)
                .integer(current.green)
                .integer(current.blue)
                .double(current.opacity)
                .send("banner:new", player)
        }
    }

    @JvmStatic
    fun remove(uuid: UUID) = banners.remove(uuid)

    @JvmStatic
    fun hide(player: Player, vararg uuid: UUID) =
        ModTransfer(uuid.size).apply { uuid.forEach { string(it.toString()) } }.send("banner:remove", player)

    @JvmStatic
    fun hide(player: Player, vararg banner: Banner) = hide(player, *banner.map { it.uuid }.toTypedArray())

    fun Banner.location(location: Location) {
        x = location.x
        y = location.y
        z = location.z
        motionSettings["yaw"] = location.yaw
        motionSettings["pitch"] = location.pitch
    }

    fun Banner.eyeLocation(location: Location) {
        motionSettings["yaw"] = Math.toDegrees(-kotlin.math.atan2(location.x - x, location.z - z)).toFloat()
        motionSettings["pitch"] = Math.toDegrees(
            kotlin.math.atan2(
                location.y - y,
                sqrt(pow(location.x - x, 2.0) + pow(location.z - z, 2.0))
            )
        )
    }

    fun Banner.target(entity: LivingEntity, offsetX: Double = 0.0, offsetY: Double = 0.0, offsetZ: Double = 0.0,) {
        motionSettings["target"] = entity.entityId
        motionSettings["offsetX"] = offsetX
        motionSettings["offsetY"] = offsetY
        motionSettings["offsetZ"] = offsetZ
    }

    fun Banner.shineBlocks(boolean: Boolean) {
        motionSettings["xray"] = boolean
    }

    fun Banner.harmonicVibrations(dimension: Dimension, amplitude: Double, speed: Double, offset: Double) {
        motionType = MotionType.PERIODIC
        motionSettings["amplitude${dimension.name}"] = amplitude
        motionSettings["speed${dimension.name}"] = speed
        motionSettings["offset${dimension.name}"] = offset
    }
}