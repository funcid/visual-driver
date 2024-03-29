package me.func.mod

import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import me.func.atlas.util.dir
import me.func.atlas.util.fileLastName
import me.func.mod.conversation.AutoSendRegistry
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.SubscribeVerifier
import me.func.mod.conversation.data.LootDrop
import me.func.mod.debug.Debug
import me.func.mod.debug.ModWatcher
import me.func.mod.graffiti.GraffitiClient
import me.func.mod.graffiti.GraffitiManager
import me.func.mod.graffiti.GraffitiManager.isCanPlace
import me.func.mod.reactive.callback.ReactivePlaceCallbackHandler
import me.func.mod.ui.Glow
import me.func.mod.ui.dialog.Dialog
import me.func.mod.ui.menu.MenuManager
import me.func.mod.ui.menu.queue.QueueViewer
import me.func.mod.util.*
import me.func.protocol.data.color.RGB
import me.func.protocol.data.status.EndStatus
import me.func.protocol.data.status.MessageStatus
import me.func.protocol.math.Position
import me.func.protocol.personalization.GraffitiPlaced
import me.func.protocol.ui.indicator.Indicators
import me.func.protocol.world.marker.Marker
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import pw.lach.p13n.network.client.P13nChannels.ENABLE_DISABLE_MODELS
import pw.lach.p13n.network.client.PacketEnableDisableModels
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Predicate

val MOD_STORAGE_URL = System.getenv("MOD_STORAGE_URL") ?: "https://storage.c7x.dev/func/animation-api/"
val MOD_LOCAL_TEST_DIR_NAME = dir(System.getenv("MOD_TEST_PATH") ?: "mods").fileLastName()
val MOD_LOCAL_DIR_NAME = dir(System.getenv("MOD_PATH") ?: "anime").fileLastName()

object Anime {

    val provided: JavaPlugin = JavaPlugin.getProvidingPlugin(this.javaClass)
    var graffitiClient: GraffitiClient? = null
    val version = readVersion()

    val STANDARD_MOD_URL = MOD_STORAGE_URL + "v$version/animation-api-production.jar"
    val GRAFFITI_MOD_URL = MOD_STORAGE_URL + "graffiti-bundle.jar"

    private fun readVersion(): String {
        val stream = this.javaClass.classLoader.getResourceAsStream("version.properties")
        val properties = Properties()
        properties.load(stream)
        return properties.getProperty("version", "error")
    }

    init {
        log("Enabling animation-api, version: $version")

        listener(StandardMods, Glow, AutoSendRegistry, SubscribeVerifier, QueueViewer)
        subscriber(GraffitiManager, Dialog, MenuManager)

        Debug // Инициализации команды и обработчика сообщений
        ReactivePlaceCallbackHandler // инициализация хука к реактивным местам
    }

    @JvmStatic
    fun include(vararg kits: Kit) {
        // Если в списке есть модуль DEBUG - устанавливаем тестовую папку
        if (kits.contains(Kit.DEBUG)) {
            warn("Running in debug mode!")
            ModLoader.loadAll(ModWatcher.testingPath)
        }

        // Метод для загрузки модулей
        fun load(kit: Kit) {
            kit.fromUrl?.let { url -> ModLoader.loadFromWeb(url) }
            kit.init()
        }

        // Загружаем все киты
        kits.filter { it != Kit.DEBUG }.forEach(::load)
    }

    // Метод для изменения правила установки граффити
    @JvmStatic
    fun modifyGraffitiPlaceCondition(canPlace: Predicate<Location>) {
        isCanPlace = canPlace
    }

    @JvmStatic
    fun sendEmptyBuffer(channel: String, player: Player) = ModTransfer().send(channel, player)

    @Deprecated("Будет переделано на полноценное меню")
    @JvmStatic
    fun openLootBox(player: Player, vararg items: LootDrop) = ModTransfer().integer(items.size).apply {
        items.forEach {
            item(it.itemStack)
                .string(it.title)
                .string(it.customRare ?: it.rare.name)
        }
    }.send("lootbox", player)

    @JvmStatic
    fun alert(player: Player, title: String, description: String, seconds: Double) = ModTransfer()
        .string(title)
        .string(description)
        .double(seconds)
        .send("func:alert", player)

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
    fun title(player: Player, vararg text: String) = title(player, text.joinToString("\n"))

    @JvmStatic
    @Deprecated(
        "Используйте ReactiveProgress",
        ReplaceWith("ReactiveProgress", "me.func.mod.reactive.ReactiveProgress")
    )
    fun timer(player: Player, text: String, duration: Int, red: Int, blue: Int, green: Int) = ModTransfer()
        .string(text)
        .integer(duration)
        .integer(red)
        .integer(blue)
        .integer(green)
        .send("func:bar", player)

    @JvmStatic
    @Deprecated(
        "Используйте ReactiveProgress",
        ReplaceWith("ReactiveProgress", "me.func.mod.reactive.ReactiveProgress")
    )
    fun timer(player: Player, text: String, duration: Int) = timer(player, text, duration, 42, 102, 189)

    @JvmStatic
    @Deprecated(
        "Используйте ReactiveProgress",
        ReplaceWith("ReactiveProgress", "me.func.mod.reactive.ReactiveProgress")
    )
    fun timer(player: Player, duration: Int) = timer(player, "До конца осталось", duration)

    @JvmStatic
    @Deprecated("Устаревший метод, новый - overlayText")
    fun bottomRightMessage(player: Player, text: String) {
        ModTransfer().integer(Position.BOTTOM_RIGHT.ordinal).string(text).send("anime:overlay", player)
    }

    @JvmStatic
    fun overlayText(player: Player, positions: Position, text: String) {
        ModTransfer().integer(positions.ordinal).string(text).send("anime:overlay", player)
    }

    @JvmStatic
    fun overlayText(player: Player, positions: Position, vararg text: String) =
        overlayText(player, positions, text.joinToString("\n"))

    @JvmStatic
    fun overlayText(players: Collection<Player>, positions: Position, text: String) {
        ModTransfer().integer(positions.ordinal).string(text).send("anime:overlay", players)
    }

    @JvmStatic
    fun overlayText(players: Collection<Player>, positions: Position, vararg text: String) =
        overlayText(players, positions, text.joinToString("\n"))

    @JvmStatic
    @Deprecated("Устаревший метод, новый - overlayText")
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
    fun killboardMessage(players: Collection<Player>, text: String, topMargin: Int) {
        ModTransfer()
            .string(text)
            .integer(topMargin)
            .send("func:notice", players)
    }

    @JvmStatic
    fun killboardMessage(players: Collection<Player>, text: String) {
        killboardMessage(players, text, 15)
    }

    @JvmStatic
    fun systemMessage(player: Player, messageStatus: MessageStatus, text: String) {
        ModTransfer().integer(messageStatus.ordinal).double(1.5).string(text).send("anime:message", player)
    }

    @JvmStatic
    fun systemMessage(player: Player, messageStatus: MessageStatus, duration: Double, text: String) {
        ModTransfer().integer(messageStatus.ordinal).double(duration).string(text).send("anime:message", player)
    }

    @JvmStatic
    fun lockPersonalization(player: Player) {
        ModTransfer(true).send("func:break-ui", player)
    }

    @JvmStatic
    fun unlockPersonalization(player: Player) {
        ModTransfer(false).send("func:break-ui", player)
    }

    @JvmStatic
    fun loadTexture(player: Player, url: String): String {
        ModTransfer()
            .string(url)
            .send("func:load-path", player)
        return "cache/animation:${url.split("/").last()}"
    }

    @JvmStatic
    fun loadTextures(player: Player, vararg url: String): List<String> {
        ModTransfer().integer(url.size).apply { url.forEach { string(it) } }.send("func:load-paths", player)
        return url.map { "cache/animation:${it.split("/").last()}" }
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
    fun topMessage(player: Player, message: String, vararg objects: Any?) = topMessage(player, message.format(objects))

    @JvmStatic
    fun cursorMessage(player: Player, message: String) {
        ModTransfer()
            .string(message)
            .send("func:cursor", player)
    }

    @JvmStatic
    fun cursorMessage(player: Player, message: String, vararg objects: Any?) =
        cursorMessage(player, message.format(objects))

    @JvmStatic
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
    fun moveMarker(player: Player, uuid: UUID, toX: Double, toY: Double, toZ: Double, seconds: Double) = ModTransfer()
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
    fun markers(player: Player, vararg markers: Marker) = markers.forEach { marker(player, it) }

    @JvmStatic
    fun removeMarker(player: Player, uuid: UUID) = ModTransfer()
        .string(uuid.toString())
        .send("func:marker-kill", player)

    @JvmStatic
    fun removeMarker(player: Player, marker: Marker) = removeMarker(player, marker.uuid)

    @JvmStatic
    fun clearMarkers(player: Player) = sendEmptyBuffer("func:clear", player)

    @JvmOverloads
    @JvmStatic
    fun itemTitle(player: Player, item: ItemStack, title: String?, subtitle: String?, duration: Double = 3.0) =
        ModTransfer()
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
    fun graffiti(player: Player, placed: GraffitiPlaced) {
        if (player.world.name != placed.world)
            return

        ModTransfer()
            .string(placed.owner.toString())
            .string(placed.ownerName)
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
            .double(placed.extraRotation)
            .boolean(placed.onGround)
            .send("graffiti:create", player)
    }

    @JvmStatic
    @Deprecated(
        "Используйте ReactiveProgress",
        ReplaceWith("ReactiveProgress", "me.func.mod.reactive.ReactiveProgress")
    )
    fun reload(player: Player, seconds: Double, text: String, red: Int, green: Int, blue: Int) = ModTransfer()
        .double(seconds)
        .string(text)
        .integer(red)
        .integer(green)
        .integer(blue)
        .send("func:recharge", player)

    @JvmStatic
    @Deprecated(
        "Используйте ReactiveProgress",
        ReplaceWith("ReactiveProgress", "me.func.mod.reactive.ReactiveProgress")
    )
    fun reload(player: Player, seconds: Double, text: String, color: RGB) =
        reload(player, seconds, text, color.red, color.green, color.blue)

    @JvmStatic
    @Deprecated(
        "Используйте ReactiveProgress",
        ReplaceWith("ReactiveProgress", "me.func.mod.reactive.ReactiveProgress")
    )
    fun reload(player: Player, seconds: Double, text: String) = reload(player, seconds, text, 255, 192, 203)

    @JvmStatic
    @JvmOverloads
    fun showEnding(
        player: Player,
        endStatus: EndStatus,
        key: String,
        value: String,
        secondsShown: Double = 8.0,
    ) {
        ModTransfer()
            .integer(endStatus.ordinal)
            .string(key)
            .string(value)
            .double(secondsShown)
            .send("crazy:ending", player)
    }

    @JvmStatic
    fun bigTitle(player: Player, message: String) = bigTitle(player, 1.5, message)

    @JvmStatic
    fun bigTitle(player: Player, duration: Double, message: String) {
        ModTransfer().double(duration).string(message).send("ilisov:bigtitle", player)
    }

    @JvmStatic
    @JvmOverloads
    fun showEnding(
        player: Player,
        endStatus: EndStatus,
        key: List<String>,
        value: List<String>,
        secondsShown: Double = 8.0,
    ) = showEnding(
        player = player,
        endStatus = endStatus,
        key = key.joinToString("\n \n"),
        value = value.joinToString("\n \n"),
        secondsShown = secondsShown,
    )

    @JvmStatic
    fun close(player: Player) {
        MenuManager.clearHistory(player)
        sendEmptyBuffer("func:close", player)
    }

    fun equipPersonalization(viewer: Player, stand: UUID, vararg personalization: UUID) {
        ModTransfer()
            .json(PacketEnableDisableModels(stand, personalization.toSet(), setOf()))
            .send(ENABLE_DISABLE_MODELS, viewer)
    }

    fun openUrl(player: Player, url: String) = ModTransfer().string(url).send("open:url", player)

    fun openP13n(player: Player) = sendEmptyBuffer("open:p13n", player)

}
