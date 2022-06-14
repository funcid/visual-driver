package me.func.mod

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import me.func.mod.Anime.graffitiClient
import me.func.mod.Npc.npcs
import me.func.mod.battlepass.BattlePass
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.graffiti.CoreGraffitiClient
import me.func.mod.graffiti.GraffitiManager
import me.func.mod.util.after
import me.func.mod.util.command
import me.func.mod.util.fileLastName
import me.func.mod.util.listener
import me.func.protocol.Mod
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import ru.cristalix.core.formatting.Formatting
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.io.path.absolutePathString
import kotlin.io.path.toPath

val STANDARD_MOD_URL = MOD_STORAGE_URL + "standard-hover-mod-bundle.jar"
val GRAFFITI_MOD_URL = MOD_STORAGE_URL + "graffiti-bundle.jar"

@PublishedApi
internal object StandardMods : Listener {
    val mods: EnumSet<Mod> = EnumSet.noneOf(Mod::class.java)

    init {
        // Если не получилось скачать мод с сервера, загрузить его из ресурсов
        ModLoader.load(ModLoader.download(STANDARD_MOD_URL).ifEmpty {
            Anime.provided.classLoader.getResource(STANDARD_MOD_URL.fileLastName())?.path ?: STANDARD_MOD_URL.fileLastName()
        })
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun PlayerJoinEvent.handle() {
        if (mods.isNotEmpty()) {
            after {
                ModLoader.send(STANDARD_MOD_URL.fileLastName(), player)

                after {
                    ModTransfer(mods.size).apply {
                        mods.forEach { integer(it.ordinal) }
                    }.send("anime:loadmod", player)
                }
            }
        }
    }
}

enum class Kit(val fromUrl: String? = null, private val setup: () -> Unit = {}) : Listener {
    STANDARD({ StandardMods.mods.add(Mod.STANDARD) }),
    LOOTBOX({ StandardMods.mods.add(Mod.LOOTBOX) }),
    DIALOG({ StandardMods.mods.add(Mod.DIALOG) }),
    EXPERIMENTAL({ StandardMods.mods.add(Mod.EXPERIMENTAL) }) {
        @EventHandler(priority = EventPriority.LOW)
        fun PlayerJoinEvent.handle() {
            after { Banners.show(player, *Banners.banners.map { it.value }.toTypedArray()) }
        }
    },
    MULTI_CHAT({ StandardMods.mods.add(Mod.CHAT) }),
    NPC({ StandardMods.mods.add(Mod.NPC) }) {
        @EventHandler(priority = EventPriority.LOW)
        fun PlayerJoinEvent.handle() {
            after(3) { npcs.forEach { (_, value) -> value.spawn(player) } }
        }

        @EventHandler
        fun PlayerUseUnknownEntityEvent.handle() {
            npcs[entityId]?.click?.accept(this)
        }

        @EventHandler
        fun PlayerChangedWorldEvent.handle() {
            // Если игрок сменил мир, отправить ему NPC в его мире
            npcs.forEach { (_, npc) -> npc.hide(player) }
            after(5) {
                npcs.filter { it.value.worldUuid == null || it.value.worldUuid == player.world.uid }
                    .forEach { (_, npc) -> npc.spawn(player) }
            }
        }

        @EventHandler
        fun PlayerRespawnEvent.handle() {
            // Если игрок умер и воскрешается, пересоздать NPC
            after { npcs.forEach { (_, npc) -> npc.hide(player) } }
            after(5) {
                npcs.filter { it.value.worldUuid == null || it.value.worldUuid == player.world.uid }
                    .forEach { (_, npc) -> npc.spawn(player) }
            }
        }
    },
    BATTLEPASS({ StandardMods.mods.add(Mod.BATTLEPASS) }) {
        @EventHandler(priority = EventPriority.LOW)
        fun PlayerJoinEvent.handle() {
            after { BattlePass.battlePasses.forEach { (_, value) -> BattlePass.send(player, value) } }
        }
    },
    HEALTH_BAR({ StandardMods.mods.add(Mod.HEALTHBAR) }),
    GRAFFITI(GRAFFITI_MOD_URL) {
        @EventHandler(priority = EventPriority.LOW)
        fun AsyncPlayerPreLoginEvent.handle() {
            // Если он на самом деле не заходит, то не грузить
            if (result == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                // Первая попытка загрузки данных
                GraffitiManager.tryPutData(uniqueId)
            }
        }

        @EventHandler
        fun PlayerQuitEvent.handle1() {
            // Очистка от игрока
            GraffitiManager.clear(player)
        }

        @EventHandler(priority = EventPriority.LOW)
        fun PlayerJoinEvent.handle() {
            // Загрузка нового клиента
            graffitiClient = graffitiClient ?: CoreGraffitiClient()

            after(5) {
                // Отправить картинку с граффити
                Anime.loadTexture(player, "https://storage.c7x.ru/func/animation-api/graffiti.png")

                // Отправить игроку мод
                ModLoader.send(GRAFFITI_MOD_URL.fileLastName(), player)

                // Отправка всех действующих граффити игроку в его мире
                GraffitiManager.sendGraffitiBulk(player)

                // Загрузка персональных граффити
                GraffitiManager.tryPutData(player.uniqueId).thenAccept { data ->

                    // Если данные игрока успешно загрузились - отправить их
                    data?.let {
                        val transfer = ModTransfer(player.uniqueId.toString(), data.packs.size)

                        data.packs.forEach { pack ->
                            transfer.string(pack.uuid.toString()).integer(pack.graffiti.size)

                            pack.graffiti.forEach { graffiti ->
                                transfer.string(graffiti.uuid.toString())
                                    .integer(graffiti.address.x)
                                    .integer(graffiti.address.y)
                                    .integer(graffiti.address.size)
                                    .integer(graffiti.address.maxUses)
                                    .string(graffiti.author)
                                    .integer(graffiti.uses)
                            }

                            transfer.string(pack.title)
                                .string(pack.creator)
                                .integer(pack.price)
                                .integer(pack.rare)
                                .boolean(pack.available)
                        }

                        transfer.integer(data.activePack).integer(data.stickers.size)

                        data.stickers.forEach {
                            transfer.string(it.uuid.toString())
                                .string(it.name)
                                .integer(it.rare.ordinal)
                                .long(it.openTime)
                        }

                        data.activeSticker?.toString()?.let { transfer.string(it) }

                        transfer.send("graffiti:init", player)
                        return@thenAccept
                    }

                    // Если же данные не загрузились
                    player.sendMessage(Formatting.error("Сервер не получил данных от сервиса граффити."))
                }
            }
        }

        @EventHandler
        fun PlayerChangedWorldEvent.handle() {
            // Если игрок был телепортирован, отправить ему граффити в его мире
            after { GraffitiManager.sendGraffitiBulk(player) }
        }
    },
    DEBUG,
    STORE({ StandardMods.mods.add(Mod.STORE) }),
    ;

    constructor(setup: () -> Unit) : this(null, setup)

    fun init() {
        setup()
        listener(this)
        command("lootboxsound") { player, _ ->
            player.playSound(
                player.location,
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                SoundCategory.MASTER,
                1f,
                2f
            )
        }
    }
}
