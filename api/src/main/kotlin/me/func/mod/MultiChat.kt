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
    private val chatsKey: MutableMap<String, ModChat> = mutableMapOf()

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
    fun createKey(id: String, chat: ModChat) {
        chatsKey[id] = chat
    }

    @JvmStatic
    fun removeKey(chat: ModChat) {
        chatsKey.entries.find { it.value == chat }?.key.let { key -> chatsKey.remove(key) }
    }

    @JvmStatic
    fun removeKey(key: String) {
        chatsKey.remove(key)
    }

    @JvmStatic
    fun removeKey(id: UUID) {
        chatsKey.entries.find { it.value.id == id }?.key.let { key -> chatsKey.remove(key) }
    }

    @JvmStatic
    fun registerHandler(chat: ModChat, consumer: BiConsumer<Player, String>) {
        handlers[chat.id] = consumer
    }

    @JvmStatic
    fun registerHandler(id: UUID, consumer: BiConsumer<Player, String>) {
        handlers[id] = consumer
    }

    @JvmStatic
    fun registerHandler(key: String, consumer: BiConsumer<Player, String>) {
        chatsKey[key]?.let { handlers[it.id] = consumer }
    }


    @JvmStatic
    fun removeHandler(chat: ModChat) {
        handlers.remove(chat.id)
    }

    @JvmStatic
    fun removeHandler(key: String) {
        chatsKey[key]?.let { handlers.remove(it.id) }
    }

    @JvmStatic
    fun removeHandler(id: UUID) {
        handlers.remove(id)
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
    fun sendChats(player: Player, vararg chats: String) {
        for (chatKey in chats) {
            chatsKey[chatKey]?.let { sendChats(player, it) }
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
    fun removeChats(player: Player, vararg chats: ModChat) {
        for (chat in chats) {
            ModTransfer()
                .string(chat.toString())
                .send("multichat:remove", player)
        }
    }

    @JvmStatic
    fun removeChats(player: Player, vararg chats: String) {
        for (chatKey in chats) {
            chatsKey[chatKey]?.let {
                ModTransfer()
                    .string(it.id.toString())
                    .send("multichat:remove", player)
            }
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
    fun sendMessage(player: Player, key: String, message: String) {
        chatsKey[key]?.let { sendMessage(player, it, message) }
    }

    @JvmStatic
    fun sendMessage(player: Player, id: UUID, message: String) {
        ModTransfer()
            .string(id.toString())
            .json(message)
            .send("multichat:message", player)
    }

    @JvmStatic
    fun broadcastMessage(players: Collection<Player>, chat: ModChat, message: String) {
        players.forEach { sendMessage(it, chat, message) }
    }

    @JvmStatic
    fun broadcastMessage(chat: ModChat, message: String) {
        broadcastMessage(Bukkit.getOnlinePlayers(), chat, message)
    }

    @JvmStatic
    fun broadcastMessage(players: Collection<Player>, key: String, message: String) {
        players.forEach { sendMessage(it, key, message) }
    }

    @JvmStatic
    fun broadcastMessage(key: String, message: String) {
        broadcastMessage(Bukkit.getOnlinePlayers(), key, message)
    }

    @JvmStatic
    fun broadcastMessage(players: Collection<Player>, id: UUID, message: String) {
        players.forEach { sendMessage(it, id, message) }
    }

    @JvmStatic
    fun broadcastMessage(id: UUID, message: String) {
        broadcastMessage(Bukkit.getOnlinePlayers(), id, message)
    }
}