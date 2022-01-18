package me.func.mod

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import me.func.mod.Anime.provided
import me.func.mod.Npc.npcs
import me.func.mod.Npc.spawn
import me.func.mod.battlepass.BattlePass
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.graffiti.GraffitiManager
import me.func.protocol.element.MotionType
import net.minecraft.server.v1_12_R1.MinecraftServer
import net.minecraft.server.v1_12_R1.SoundEffects.id
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import ru.cristalix.core.formatting.Formatting


enum class Kit(val fromUrl: String) : Listener {

    STANDARD("https://implario.dev/animation-api/mod-bundle-v1.jar") {
        @EventHandler(priority = EventPriority.HIGHEST)
        fun PlayerJoinEvent.handle() {
            MinecraftServer.SERVER.postToMainThread {
                ModLoader.send("mod-bundle-v1.jar", player)
            }
        }
    },
    LOOTBOX("https://implario.dev/animation-api/cristalix-lootbox.jar") {
        @EventHandler(priority = EventPriority.HIGHEST)
        fun PlayerJoinEvent.handle() {
            MinecraftServer.SERVER.postToMainThread {
                ModLoader.send("cristalix-lootbox.jar", player)
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        fun PlayerCommandPreprocessEvent.handle() {
            if (!cancel && message == "lootboxsound")
                player.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1f, 2f)
        }
    },
    DIALOG("https://implario.dev/animation-api/cristalix-dialog.jar") {
        @EventHandler(priority = EventPriority.HIGHEST)
        fun PlayerJoinEvent.handle() {
            MinecraftServer.SERVER.postToMainThread {
                ModLoader.send("cristalix-dialog.jar", player)
            }
        }
    },
    EXPERIMENTAL("https://implario.dev/animation-api/experimental-bundle.jar") {
        @EventHandler(priority = EventPriority.HIGHEST)
        fun PlayerJoinEvent.handle() {
            MinecraftServer.SERVER.postToMainThread {
                ModLoader.send("experimental-bundle.jar", player)

                Banners.show(player, *Banners.banners.values.toTypedArray())
            }
        }
    },
    MULTI_CHAT("https://implario.dev/animation-api/multichat-bundle.jar") {
        @EventHandler(priority = EventPriority.HIGHEST)
        fun PlayerJoinEvent.handle() {
            MinecraftServer.SERVER.postToMainThread {
                ModLoader.send("multichat-bundle.jar", player)
            }
        }
    },
    NPC("https://implario.dev/animation-api/npc-bundle.jar") {
        @EventHandler(priority = EventPriority.HIGHEST)
        fun PlayerJoinEvent.handle() {
            MinecraftServer.SERVER.postToMainThread {
                ModLoader.send("npc-bundle.jar", player)

                npcs.values.forEach { it.spawn(player) }
            }
        }

        @EventHandler
        fun PlayerUseUnknownEntityEvent.handle() {
            npcs[entityId]?.click?.accept(this)
        }
    },
    BATTLEPASS("https://implario.dev/animation-api/battlepass-bundle.jar") {
        @EventHandler(priority = EventPriority.HIGHEST)
        fun PlayerJoinEvent.handle() {
            MinecraftServer.SERVER.postToMainThread {
                ModLoader.send("battlepass-bundle.jar", player)

                BattlePass.battlePasses.values.forEach { BattlePass.send(player, it) }
            }
        }
    },
    GRAFFITI("https://implario.dev/animation-api/graffiti-bundle.jar") {
        @EventHandler
        fun AsyncPlayerPreLoginEvent.handle() {
            // Если он на самом деле не заходит, то не грузить
            if (result == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                // Первая попытка загрузки данных
                GraffitiManager.tryPutData(uniqueId)
            }
        }

        @EventHandler
        fun PlayerQuitEvent.handle() {
            // Очистка от игрока
            GraffitiManager.clear(player)
        }

        @EventHandler
        fun PlayerJoinEvent.handle() {
            Bukkit.getScheduler().runTaskLater(provided, {
                // Отправить картинку с граффити
                Anime.loadTexture(player, "https://implario.dev/animation-api/graffiti.png")

                // Отправить игроку мод
                ModLoader.send("graffiti-bundle.jar", player)

                // Отправка всех действующих граффити игроку в его мире
                GraffitiManager.sendGraffitiBulk(player)

                // Загрузка персональных граффити
                GraffitiManager.tryPutData(player.uniqueId).thenAccept { data ->
                    // Если данные игрока успешно загрузились - отправить их
                    data?.let {
                        val transfer = ModTransfer(player.uniqueId.toString(), data.packs.size)

                        data.packs.forEach { pack ->
                            transfer.string(pack.getUuid().toString()).integer(pack.graffiti.size)

                            pack.graffiti.forEach { graffiti ->
                                transfer.string(graffiti.getUuid().toString())
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

                        transfer.integer(data.activePack).send("graffiti:init", player)
                        return@thenAccept
                    }
                    // Если же данные не загрузились
                    player.sendMessage(Formatting.error("Сервер не получил данных от сервиса граффити."))
                }
            }, 5)
        }

        @EventHandler
        fun PlayerChangedWorldEvent.handle() {
            // Если игрок был телепортирован, отправить ему граффити в его мире
            MinecraftServer.SERVER.postToMainThread { GraffitiManager.sendGraffitiBulk(player) }
        }
    };

    fun init() {
        getPluginManager().registerEvents(this, provided)
    }
}