package me.func.mod.ui

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.data.MouseButton
import me.func.mod.reactive.ReactiveBanner
import me.func.mod.util.warn
import me.func.protocol.data.element.MotionType
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.function.BiConsumer

object ReactiveBanners {

    var banners = hashMapOf<UUID, ReactiveBanner>() // banner uuid to uuid
    private val clickable = hashMapOf<UUID, BiConsumer<Player, MouseButton>>() // banner uuid to consumer

    init {
        Anime.createReader("banner:react-click") { player, buffer ->
            try {

                val uuid = UUID(buffer.readLong(), buffer.readLong())
                val mouseButton = MouseButton.values()[buffer.readInt()]

                val logic = clickable[uuid] ?: return@createReader

                if (banners.containsKey(uuid)) logic.accept(player, mouseButton)

            } catch (exception: Exception) {
                warn("Error while read banner uuid: " + exception.message)
            }
        }
    }

    @JvmStatic
    fun clearAll() = banners.values.forEach { it.remove() }

    @JvmStatic
    fun ReactiveBanner.onClick(consumer: BiConsumer<Player, MouseButton>) {
        clickable[this.uuid] = consumer
    }

    @JvmStatic
    fun ReactiveBanner.hide(vararg players: Player) = ModTransfer().integer(1).uuid(this.uuid).send("banner:reactive-remove", *players)

    @JvmStatic
    fun ReactiveBanner.remove() = this.hide(*this.subscribed.mapNotNull { Bukkit.getPlayer(it) }.toTypedArray())

    @JvmStatic
    fun ReactiveBanner.show(vararg players: Player) {

        if (banners.size > 300) {
            val unique = banners.values.distinctBy { it.x }.distinctBy { it.y }.distinctBy { it.z }
            warn("Fatal error: banners map size>300! Found and cleared ${banners.size - unique.size} unused banners!")
            banners = HashMap(unique.associateBy { this.uuid })
        }

        if (!banners.keys.contains(this.uuid)) banners[this.uuid] = this

        players.filter { !this.subscribed.contains(it.uniqueId) }.forEach { this.subscribed.add(it.uniqueId) }

        ModTransfer().integer(1)
            .uuid(this.uuid)
            .integer(this.motionType.ordinal)
            .boolean(this.watchingOnPlayer)
            .boolean(this.watchingOnPlayerWithoutPitch)
            .double(-this.motionSettings["yaw"].toString().toDouble())
            .double(this.motionSettings["pitch"].toString().toDouble())
            .boolean(this.motionSettings["xray"].toString().toBoolean())
            .string(this.content)
            .v3(this.x, this.y, this.z)
            .integer(this.height)
            .integer(this.weight)
            .string(this.texture)
            .rgb(this.color)
            .double(this.opacity)
            .double(this.carveSize)
            .apply {
                if (this@show.motionType == MotionType.STEP_BY_TARGET) {
                    integer(this@show.motionSettings["target"].toString().toInt())
                    double(this@show.motionSettings["offsetX"].toString().toDouble())
                    double(this@show.motionSettings["offsetY"].toString().toDouble())
                    double(this@show.motionSettings["offsetZ"].toString().toDouble())
                }
            }.send("banner:reactive-show", *players)

        if (this.motionSettings.containsKey("line")) {

            val sizes = this.motionSettings["line"] as MutableList<Pair<Int, Double>>
            val sizeTransfer = ModTransfer().uuid(this.uuid).integer(sizes.size)

            sizes.forEach { sizeTransfer.integer(it.first).double(it.second) }
            sizeTransfer.send("banner:reactive-text", *players)
        }
    }
}