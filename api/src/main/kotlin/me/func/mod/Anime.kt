package me.func.mod

import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.data.DailyReward
import me.func.mod.data.LootDrop
import me.func.mod.debug.ModWatcher
import me.func.mod.graffiti.GraffitiClient
import me.func.mod.util.log
import me.func.mod.util.dir
import me.func.mod.util.fileLastName
import me.func.mod.util.warn
import me.func.protocol.*
import me.func.protocol.dialog.Dialog
import me.func.protocol.personalization.GraffitiPlaced
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.awt.Color
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.function.BiConsumer
import kotlin.io.path.fileSize
import kotlin.io.path.name
import kotlin.math.round

val MOD_STORAGE_URL = System.getenv("MOD_STORAGE_URL") ?: "https://storage.c7x.ru/func/animation-api/"
val MOD_LOCAL_TEST_DIR_NAME = dir(System.getenv("MOD_TEST_PATH") ?: "mods").fileLastName()
val MOD_LOCAL_DIR_NAME = dir(System.getenv("MOD_PATH") ?: "anime").fileLastName()

object Anime {

    val provided: JavaPlugin = JavaPlugin.getProvidingPlugin(this.javaClass)
    var graffitiClient: GraffitiClient? = null

    @JvmStatic
    fun include(vararg kits: Kit) {
        if (kits.contains(Kit.DEBUG)) {
            warn("Running in debug mode!")
            ModLoader.loadAll(ModWatcher.testingPath)
        }

        kits.filter { it != Kit.DEBUG }
            .onEach {
                it.fromUrl?.let { url -> ModLoader.loadFromWeb(url, MOD_LOCAL_DIR_NAME) }
                it.init()
            }
    }

    @JvmStatic
    fun sendEmptyBuffer(channel: String, player: Player) =
        ModTransfer().send(channel, player)

    @JvmStatic
    fun openLootBox(player: Player, vararg items: LootDrop) {
        val data = ModTransfer().integer(items.size)
        for (item in items) {
            data.item(item.itemStack)
                .string(item.title)
                .string(item.rare.name)
        }
        data.send("lootbox", player)
    }

    @JvmStatic
    fun dialog(player: Player, dialog: Dialog, openEntrypoint: String) {
        sendDialog(player, dialog)
        openDialog(player, openEntrypoint)
    }

    @JvmStatic
    fun sendDialog(player: Player, dialog: Dialog) {
        ModTransfer()
            .string("load")
            .json(dialog)
            .send("rise:dialog-screen", player)
    }

    @JvmStatic
    fun openDialog(player: Player, dialogId: String) {
        ModTransfer()
            .string("open")
            .string(dialogId)
            .send("rise:dialog-screen", player)
    }

    @JvmStatic
    fun alert(player: Player, title: String, description: String, seconds: Double) {
        ModTransfer()
            .string(title)
            .string(description)
            .double(seconds)
            .send("func:alert", player)
    }

    @JvmStatic
    fun alert(player: Player, title: String, description: String) {
        alert(player, title, description, 7.3)
    }

    @JvmStatic
    fun alert(player: Player, description: String, seconds: Double) {
        alert(player, "Внимание!", description, seconds)
    }

    @JvmStatic
    fun alert(player: Player, description: String) {
        alert(player, "Внимание!", description, 7.3)
    }

    @JvmStatic
    fun counting321(player: Player) {
        sendEmptyBuffer("func:attention", player)
    }

    @JvmStatic
    fun title(player: Player, text: String) {
        ModTransfer()
            .string(text)
            .send("func:title", player)
    }

    @JvmStatic
    fun title(player: Player, vararg text: String) {
        title(player, text.joinToString { "\n" })
    }

    @JvmStatic
    fun timer(player: Player, text: String, duration: Int, red: Int, blue: Int, green: Int) {
        ModTransfer()
            .string(text)
            .integer(duration)
            .integer(red)
            .integer(blue)
            .integer(green)
            .send("func:bar", player)
    }

    @JvmStatic
    fun timer(player: Player, text: String, duration: Int) {
        ModTransfer()
            .string(text)
            .integer(duration)
            .integer(42)
            .integer(102)
            .integer(189)
            .send("func:bar", player)
    }

    @JvmStatic
    fun timer(player: Player, duration: Int) {
        ModTransfer()
            .string("До конца осталось")
            .integer(duration)
            .send("func:bar", player)
    }

    @JvmStatic
    fun bottomRightMessage(player: Player, text: String) {
        ModTransfer(text).send("func:bottom", player)
    }

    @JvmStatic
    fun bottomRightMessage(player: Player, vararg text: String) = bottomRightMessage(player, text.joinToString("\n"))

    @JvmStatic
    fun killboardMessage(player: Player, text: String, topMargin: Int) {
        ModTransfer()
            .string(text)
            .integer(topMargin)
            .send("func:notice", player)
    }

    @JvmStatic
    fun killboardMessage(player: Player, text: String) {
        killboardMessage(player, text, 15)
    }

    @JvmStatic
    fun lockPersonalization(player: Player) {
        sendEmptyBuffer("func:break-ui", player)
    }

    @JvmStatic
    fun unlockPersonalization(player: Player) {
        sendEmptyBuffer("func:return-ui", player)
    }

    @JvmStatic
    fun loadTexture(player: Player, url: String) {
        ModTransfer()
            .string(url)
            .send("func:load-path", player)
    }

    @JvmStatic
    fun loadTextures(player: Player, vararg url: String) {
        val transfer = ModTransfer()
            .integer(url.size)
        for (one in url)
            transfer.string(one)
        transfer.send("func:load-paths", player)
    }

    @JvmStatic
    fun createReader(channel: String, listener: BiConsumer<Player, ByteBuf>) {
        Bukkit.getMessenger().registerIncomingPluginChannel(provided, channel) { _, player, data ->
            // Если входящий буффер слишком большой
            if (data.size > 1024)
                return@registerIncomingPluginChannel

            listener.accept(player, Unpooled.wrappedBuffer(data))
        }
    }

    @JvmStatic
    fun safeReadUUID(buffer: ByteBuf): UUID? {
        // Если указанная строка UUID
        return try {
            UUID.fromString(NetUtil.readUtf8(buffer))
        } catch (exception: Exception) {
            null
        }
    }

    @JvmStatic
    fun topMessage(player: Player, message: String) {
        ModTransfer()
            .string(message)
            .send("func:top-alert", player)
    }

    @JvmStatic
    fun cursorMessage(player: Player, message: String) {
        ModTransfer()
            .string(message)
            .send("func:cursor", player)
    }

    @JvmStatic
    @Deprecated("Маркеры устарели, существует более мощный инструмент - Banners")
    fun marker(player: Player, marker: Marker): Marker {
        ModTransfer()
            .string(marker.uuid.toString())
            .double(marker.x)
            .double(marker.y)
            .double(marker.z)
            .double(marker.scale)
            .string(marker.texture)
            .send("func:marker-new", player)
        return marker
    }

    @JvmStatic
    fun moveMarker(player: Player, uuid: UUID, toX: Double, toY: Double, toZ: Double, seconds: Double) =
        ModTransfer()
            .string(uuid.toString())
            .double(toX)
            .double(toY)
            .double(toZ)
            .double(seconds)
            .send("func:marker-move", player)

    @JvmStatic
    fun moveMarker(player: Player, marker: Marker, seconds: Double) =
        moveMarker(player, marker.uuid, marker.x, marker.y, marker.z, seconds)

    @JvmStatic
    fun moveMarker(player: Player, marker: Marker) =
        moveMarker(player, marker.uuid, marker.x, marker.y, marker.z, 0.01)

    @JvmStatic
    @Deprecated("Маркеры устарели, существует более мощный инструмент - Banners")
    fun markers(player: Player, vararg markers: Marker) = markers.forEach { marker(player, it) }

    @JvmStatic
    fun removeMarker(player: Player, uuid: UUID) = ModTransfer()
        .string(uuid.toString())
        .send("func:marker-kill", player)

    @JvmStatic
    fun removeMarker(player: Player, marker: Marker) = removeMarker(player, marker.uuid)

    @JvmStatic
    fun clearMarkers(player: Player) = sendEmptyBuffer("func:clear", player)

    @JvmStatic
    fun openDailyRewardMenu(player: Player, currentDayIndex: Int, vararg week: DailyReward) {
        if (week.size != 7) {
            throw IllegalArgumentException("Week size must be 7!")
        }

        val transfer = ModTransfer().integer(currentDayIndex + 1)
        for (day in week)
            transfer.item(CraftItemStack.asNMSCopy(day.icon))
                .string("§7Награда: " + day.title)
        transfer.send("func:weekly-reward", player)
    }

    @JvmStatic
    fun itemTitle(player: Player, item: ItemStack, title: String?, subtitle: String?, duration: Double) = ModTransfer()
        .item(item)
        .string(title ?: "")
        .string(subtitle ?: "")
        .double(duration)
        .send("func:drop-item", player)

    @JvmStatic
    fun hideIndicator(player: Player, vararg indicator: Indicators) {
        for (one in indicator) {
            ModTransfer()
                .integer(one.ordinal)
                .send("func:hide-it", player)
        }
    }

    @JvmStatic
    fun showIndicator(player: Player, vararg indicator: Indicators) {
        for (one in indicator) {
            ModTransfer()
                .integer(one.ordinal)
                .send("func:show-it", player)
        }
    }

    @JvmStatic
    fun clearAllCorpses(player: Player) = sendEmptyBuffer("func:corpse-clear", player)

    @JvmStatic
    fun corpse(to: Player, name: String?, uuid: UUID, x: Double, y: Double, z: Double, secondsAlive: Int = 60) =
        corpse(to, name ?: "", "https://webdata.c7x.dev/textures/skin/$uuid", x, y, z, secondsAlive)

    @JvmStatic
    fun corpse(to: Player, name: String?, skinUrl: String, x: Double, y: Double, z: Double, secondsAlive: Int = 60) =
        ModTransfer()
            .string(name ?: "")
            .string(skinUrl)
            .string(skinUrl.substring(skinUrl.lastIndexOf("/") + 1))
            .double(x)
            .double(y + 3)
            .double(z)
            .integer(secondsAlive)
            .send("func:corpse", to)

    @JvmStatic
    fun sphere(to: Player, uuid: UUID, x: Double, y: Double, z: Double, color: Color, diameter: Double) =
        ModTransfer()
            .integer(0)
            .long(uuid.mostSignificantBits)
            .long(uuid.leastSignificantBits)
            .double(x)
            .double(y)
            .double(z)
            .integer(color.red)
            .integer(color.green)
            .integer(color.blue)
            .double(color.alpha / 255.0)
            .double(diameter)
            .send("fiwka:sphere", to)

    @JvmStatic
    fun sphere(
        to: Player,
        uuid: UUID,
        x: Double,
        y: Double,
        z: Double,
        color: Color,
        sX: Double,
        sY: Double,
        sZ: Double
    ) =
        ModTransfer()
            .integer(1)
            .long(uuid.mostSignificantBits)
            .long(uuid.leastSignificantBits)
            .double(x)
            .double(y)
            .double(z)
            .integer(color.red)
            .integer(color.green)
            .integer(color.blue)
            .double(color.alpha / 255.0)
            .double(sX)
            .double(sY)
            .double(sZ)
            .send("fiwka:sphere", to)

    @JvmStatic
    fun teleportSphereTo(to: Player, uuid: UUID, x: Double, y: Double, z: Double) =
        ModTransfer()
            .integer(2)
            .long(uuid.mostSignificantBits)
            .long(uuid.leastSignificantBits)
            .double(x)
            .double(y)
            .double(z)
            .send("fiwka:sphere", to)

    @JvmStatic
    fun moveSphereTo(to: Player, uuid: UUID, x: Double, y: Double, z: Double, time: Double) =
        ModTransfer()
            .integer(3)
            .long(uuid.mostSignificantBits)
            .long(uuid.leastSignificantBits)
            .double(x)
            .double(y)
            .double(z)
            .double(time)
            .send("fiwka:sphere", to)

    @JvmStatic
    fun sphere(to: Player, uuid: UUID, location: Location, color: Color, radius: Double) =
        sphere(to, uuid, location.getX(), location.getY(), location.getZ(), color, radius)

    @JvmStatic
    fun sphere(to: Player, uuid: UUID, location: Location, color: Color, sX: Double, sY: Double, sZ: Double) =
        sphere(to, uuid, location.getX(), location.getY(), location.getZ(), color, sX, sY, sZ)

    @JvmStatic
    fun teleportSphereTo(to: Player, uuid: UUID, location: Location) =
        teleportSphereTo(to, uuid, location.getX(), location.getY(), location.getZ())

    @JvmStatic
    fun moveSphereTo(to: Player, uuid: UUID, location: Location, time: Double) =
        moveSphereTo(to, uuid, location.getX(), location.getY(), location.getZ(), time)

    @JvmStatic
    fun removeSphere(to: Player, uuid: UUID) =
        ModTransfer()
            .integer(4)
            .long(uuid.mostSignificantBits)
            .long(uuid.leastSignificantBits)
            .send("fiwka:sphere", to)

    @JvmStatic
    fun chat(player: Player, chat: ModChat, message: String) {
        ModTransfer()
            .integer(chat.ordinal + 1)
            .json(message)
            .send("zabelix:chat_message", player)
    }

    @JvmStatic
    fun removeChats(player: Player, vararg chats: ModChat) {
        for (chat in chats) {
            ModTransfer()
                .integer(chat.ordinal + 1)
                .send("delete-chat", player)
        }
    }

    @JvmStatic
    fun graffiti(player: Player, placed: GraffitiPlaced) {
        if (player.world.name != placed.world)
            return

        ModTransfer()
            .string(placed.owner.toString())
            .string(placed.world)
            .string(placed.graffiti.uuid.toString())
            .integer(placed.graffiti.address.x)
            .integer(placed.graffiti.address.y)
            .integer(placed.graffiti.address.size)
            .integer(placed.graffiti.address.maxUses)
            .string(placed.graffiti.author)
            .integer(placed.graffiti.uses)
            .double(placed.x)
            .double(placed.y)
            .double(placed.z)
            .integer(placed.ticksLeft)
            .double(placed.rotationAngle)
            .double(placed.rotationAxisX)
            .double(placed.rotationAxisY)
            .double(placed.rotationAxisZ)
            .boolean(placed.onGround)
            .boolean(placed.local)
            .send("graffiti:create", player)
    }

    @JvmStatic
    fun reload(player: Player, seconds: Double, text: String, red: Int, green: Int, blue: Int) {
        ModTransfer()
            .double(seconds)
            .string(text)
            .integer(red)
            .integer(green)
            .integer(blue)
            .send("func:recharge", player)
    }

    @JvmStatic
    fun reload(player: Player, seconds: Double, text: String, glowColor: GlowColor) =
        reload(player, seconds, text, glowColor.red, glowColor.green, glowColor.blue)

    @JvmStatic
    fun reload(player: Player, seconds: Double, text: String) = reload(player, seconds, text, 255, 192, 203)

    @JvmStatic
    fun showEnding(player: Player, endStatus: EndStatus, key: String, value: String) {
        ModTransfer()
            .integer(endStatus.ordinal)
            .string(key)
            .string(value)
            .send("crazy:ending", player)
    }

    @JvmStatic
    fun bigTitle(player: Player, message: String) {
        ModTransfer()
            .string(message)
            .send("ilisov:bigtitle", player)
    }

    @JvmStatic
    fun showEnding(player: Player, endStatus: EndStatus, key: List<String>, value: List<String>) =
        showEnding(player, endStatus, key.joinToString("\n \n"), value.joinToString("\n \n"))
}