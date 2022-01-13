package me.func.mod

import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import me.func.mod.Anime.provided
import me.func.mod.Anime.sendEmptyBuffer
import me.func.mod.Banners.eyeLocation
import me.func.mod.Banners.location
import me.func.mod.Banners.target
import me.func.mod.Npc.npc
import me.func.mod.Npc.onClick
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.data.DailyReward
import me.func.mod.data.LootDrop
import me.func.mod.graffiti.DefaultGraffitiClient
import me.func.mod.graffiti.GraffitiClient
import me.func.protocol.Indicators
import me.func.protocol.Marker
import me.func.protocol.ModChat
import me.func.protocol.dialog.Dialog
import me.func.protocol.graffiti.GraffitiPlaced
import me.func.protocol.npc.NpcBehaviour
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.BiConsumer

object Anime {

    val provided: JavaPlugin = JavaPlugin.getProvidingPlugin(this.javaClass)
    var graffitiClient: GraffitiClient? = null

    @JvmStatic
    fun include(vararg kits: Kit) {
        for (kit in kits) {
            ModLoader.loadFromWeb(kit.fromUrl, "anime")
            kit.init()

            if (kit == Kit.GRAFFITI) {
                graffitiClient = graffitiClient ?: DefaultGraffitiClient(
                    System.getenv("GRAFFITI_SERVICE_HOST"),
                    System.getenv("GRAFFITI_SERVICE_PASSWORD"),
                    System.getenv("GRAFFITI_SERVICE_PORT").toInt(),
                    UUID.randomUUID().toString()
                ).connect()
            }
        }
    }

    @JvmStatic
    fun sendEmptyBuffer(channel: String, player: Player) {
        ModTransfer().send(channel, player)
    }

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
    fun moveMarker(player: Player, uuid: UUID, toX: Double, toY: Double, toZ: Double, seconds: Double) {
        ModTransfer()
            .string(uuid.toString())
            .double(toX)
            .double(toY)
            .double(toZ)
            .double(seconds)
            .send("func:marker-move", player)
    }

    @JvmStatic
    fun moveMarker(player: Player, marker: Marker, seconds: Double): Marker {
        moveMarker(player, marker.uuid, marker.x, marker.y, marker.z, seconds)
        return marker
    }

    @JvmStatic
    fun moveMarker(player: Player, marker: Marker): Marker {
        moveMarker(player, marker.uuid, marker.x, marker.y, marker.z, 0.01)
        return marker
    }

    @JvmStatic
    @Deprecated("Маркеры устарели, существует более мощный инструмент - Banners")
    fun markers(player: Player, vararg markers: Marker) {
        for (marker in markers)
            marker(player, marker)
    }

    @JvmStatic
    fun removeMarker(player: Player, uuid: UUID) {
        ModTransfer()
            .string(uuid.toString())
            .send("func:marker-kill", player)
    }

    @JvmStatic
    fun removeMarker(player: Player, marker: Marker): Marker {
        removeMarker(player, marker.uuid)
        return marker
    }

    @JvmStatic
    fun clearMarkers(player: Player) {
        sendEmptyBuffer("func:clear", player)
    }

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
    fun itemTitle(player: Player, item: ItemStack, title: String?, subtitle: String?, duration: Double) {
        ModTransfer()
            .item(item)
            .string(title ?: "")
            .string(subtitle ?: "")
            .double(duration)
            .send("func:drop-item", player)
    }

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
    fun clearAllCorpses(player: Player) {
        sendEmptyBuffer("func:corpse-clear", player)
    }

    @JvmStatic
    fun corpse(to: Player, name: String?, uuid: UUID, x: Double, y: Double, z: Double, secondsAlive: Int = 60) {
        corpse(to, name ?: "", "https://webdata.c7x.dev/textures/skin/$uuid", x, y, z, secondsAlive)
    }

    @JvmStatic
    fun corpse(to: Player, name: String?, skinUrl: String, x: Double, y: Double, z: Double, secondsAlive: Int = 60) {
        ModTransfer()
            .string(name ?: "")
            .string(skinUrl)
            .string(skinUrl.substring(skinUrl.lastIndexOf("/") + 1))
            .double(x)
            .double(y + 3)
            .double(z)
            .integer(secondsAlive)
            .send("func:corpse", to)
    }

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
            .string(placed.graffiti.getUuid().toString())
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
}