package me.func.mod.world

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.mod.conversation.data.MouseButton
import me.func.mod.reactive.ReactiveBanner
import me.func.mod.util.subscriber
import me.func.mod.util.warn
import me.func.protocol.math.Dimension
import me.func.protocol.data.element.Banner
import me.func.protocol.data.element.MotionType
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.lang.StrictMath.pow
import java.util.*
import java.util.function.BiConsumer
import kotlin.collections.HashMap
import kotlin.math.sqrt

object Banners : PlayerSubscriber {

    var banners = hashMapOf<UUID, Banner>() // banner uuid to uuid
    val clickable = hashMapOf<UUID, BiConsumer<Player, MouseButton>>() // banner uuid to consumer
    val activeHandlers = hashMapOf<UUID, MutableSet<UUID>>() // player uuid to set of uuid banners

    init {
        subscriber(this)

        Anime.createReader("banner:click") { player, buffer ->
            try {

                val uuid = UUID(buffer.readLong(), buffer.readLong())
                val mouseButton = MouseButton.values()[buffer.readInt()]
                val logic = clickable[uuid] ?: return@createReader

                if (banners.containsKey(uuid) || activeHandlers[player.uniqueId]?.contains(uuid) == true)
                    logic.accept(player, mouseButton)
            } catch (exception: Exception) {
                warn("Error while read banner uuid: " + exception.message)
            }
        }
    }

    @JvmSynthetic
    fun new(data: Banner.() -> Unit) = new(Banner().apply(data))

    /**
     * Java compatibility
     * @see Banners.add
     */
    @JvmStatic
    fun add(banner: Banner): Banner = new(banner)

    @JvmStatic
    @JvmSynthetic
    fun new(banner: Banner): Banner {

        if (banners.size > 300) {
            val unique = banners.values.distinctBy { it.x }.distinctBy { it.y }.distinctBy { it.z }
            warn("Fatal error: banners map size>300! Found and cleared ${banners.size - unique.size} unused banners!")
            banners = HashMap(unique.associateBy { banner.uuid })
            return banner
        }

        banners[banner.uuid] = banner
        return banner
    }

    @JvmStatic
    fun add(banner: Banner, consumer: BiConsumer<Player, MouseButton>): Banner {
        clickable[banner.uuid] = consumer
        return new(banner)
    }

    @JvmStatic
    fun content(player: Player, uuid: UUID, content: String) {
        banners[uuid]?.content = content
        ModTransfer().uuid(uuid).integer(1).string(content).send("banner:update", player)
    }

    @JvmStatic
    fun content(player: Player, banner: Banner, content: String) = content(player, banner.uuid, content)

    @JvmStatic
    fun ReactiveBanner.send(player: Player) {
        this.subscribed.add(player.uniqueId)
        show(player, this)
    }

    @JvmStatic
    fun ReactiveBanner.hide(player: Player) {
        this.subscribed.remove(player.uniqueId)
        hide(player, this)
    }

    @JvmStatic
    fun show(player: Player, banner: Banner, consumer: BiConsumer<Player, MouseButton>) {

        val set = activeHandlers[player.uniqueId] ?: hashSetOf()

        set.add(banner.uuid)
        activeHandlers[player.uniqueId] = set
        clickable[banner.uuid] = consumer
        show(player, banner)
    }

    @JvmStatic
    fun show(player: Player, vararg uuid: UUID) = show(player, *uuid.mapNotNull { banners[it] }.toTypedArray())

    @JvmStatic
    fun show(player: Player, vararg banner: Banner) {

        val transfer = ModTransfer().integer(banner.size)

        banner.forEach { current ->
            transfer.uuid(current.uuid)
                .integer(current.motionType.ordinal)
                .boolean(current.watchingOnPlayer)
                .boolean(current.watchingOnPlayerWithoutPitch)
                .double(-current.motionSettings["yaw"].toString().toDouble())
                .double(current.motionSettings["pitch"].toString().toDouble())
                .boolean(current.motionSettings["xray"].toString().toBoolean())
                .string(current.content)
                .v3(current.x, current.y, current.z)
                .integer(current.height)
                .integer(current.weight)
                .string(current.texture)
                .rgb(current.color)
                .double(current.opacity)
                .double(current.carveSize)
                .apply {
                    if (current.motionType == MotionType.STEP_BY_TARGET) {
                        integer(current.motionSettings["target"].toString().toInt())
                        double(current.motionSettings["offsetX"].toString().toDouble())
                        double(current.motionSettings["offsetY"].toString().toDouble())
                        double(current.motionSettings["offsetZ"].toString().toDouble())
                    }
                }
        }

        transfer.send("banner:new", player)

        banner.filter { it.motionSettings.containsKey("line") }.forEach { current ->
            val sizes = current.motionSettings["line"] as MutableList<Pair<Int, Double>>
            val sizeTransfer = ModTransfer(current.uuid.toString(), sizes.size)
            sizes.forEach { sizeTransfer.integer(it.first).double(it.second) }
            sizeTransfer.send("banner:size-text", player)
        }
    }

    @JvmStatic
    fun showAll(player: Player) = show(player, *banners.values.toTypedArray())

    @JvmStatic
    fun remove(uuid: UUID) {
        banners.remove(uuid)
        clickable.remove(uuid)
    }

    @JvmStatic
    fun hide(player: Player, vararg uuid: UUID) = ModTransfer(uuid.size).apply { uuid.forEach { uuid(it) } }.send("banner:remove", player)

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

    fun Banner.target(entity: LivingEntity, offsetX: Double = 0.0, offsetY: Double = 0.0, offsetZ: Double = 0.0) {
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

    override val isConstant = true

    override fun removeSubscriber(player: Player) {
        activeHandlers[player.uniqueId]?.forEach { uuid -> clickable.remove(uuid) }
        activeHandlers.remove(player.uniqueId)
    }

    override fun getSubscribersCount() = activeHandlers.size

    override val uuid: UUID = UUID.randomUUID()
}
