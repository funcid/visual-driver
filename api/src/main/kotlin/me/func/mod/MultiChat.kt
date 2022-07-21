package me.func.mod

import dev.xdark.feder.NetUtil
import me.func.mod.conversation.ModTransfer
import me.func.protocol.ModChat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.function.BiConsumer

object MultiChat {
    private val handlers: MutableMap<UUID, BiConsumer<Player, String>> = mutableMapOf()

    init {
        Anime.createReader("multichat:message") { player, byteBuf ->
            val id = UUID.fromString(NetUtil.readUtf8(byteBuf))
            val message = NetUtil.readUtf8(byteBuf)
            if (handlers.contains(id)) {
                handlers[id]?.accept(player, message)
            }
        }
    }

    @JvmStatic
    fun sendChats(player: Player, vararg chats: ModChat) {
        for (chat in chats) {
            ModTransfer()
                .string(chat.id.toString())
                .string(chat.name)
                .string(chat.symbol)
                .send("multichat:create", player)
        }
    }

    @JvmStatic
    fun removeChats(player: Player, vararg chats: ModChat) {
        for (chat in chats) {
            ModTransfer()
                .string(chat.id.toString())
                .send("multichat:remove", player)
        }
    }

    @JvmStatic
    fun removeChats(player: Player, vararg chats: UUID) {
        for (chat in chats) {
            ModTransfer()
                .string(chat.toString())
                .send("multichat:remove", player)
        }
    }

    @JvmStatic
    fun sendMessage(player: Player, chat: ModChat, message: String) {
        ModTransfer()
            .string(chat.id.toString())
            .json(message)
            .send("multichat:message", player)
    }

    @JvmStatic
    fun broadcastMessage(players: Collection<Player>, chat: ModChat, message: String) {
        players.forEach { sendMessage(it, chat, message) }
    }

    @JvmStatic
    fun broadcastMessage(chat: ModChat, message: String) {
        Bukkit.getOnlinePlayers().forEach { sendMessage(it, chat, message) }
    }

    @JvmStatic
    fun sendMessage(player: Player, chat: UUID, message: String) {
        ModTransfer()
            .string(chat.toString())
            .json(message)
            .send("multichat:message", player)
    }

    @JvmStatic
    fun broadcastMessage(players: Collection<Player>, chat: UUID, message: String) {
        players.forEach { sendMessage(it, chat, message) }
    }

    @JvmStatic
    fun broadcastMessage(chat: UUID, message: String) {
        Bukkit.getOnlinePlayers().forEach { sendMessage(it, chat, message) }
    }

    @JvmStatic
    fun registerHandler(chat: ModChat, consumer: BiConsumer<Player, String>) {
        handlers[chat.id] = consumer
    }
}