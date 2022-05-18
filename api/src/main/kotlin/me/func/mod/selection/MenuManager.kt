package me.func.mod.selection

import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.warn
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

inline fun selection(setup: Selection.() -> Unit) = Selection().also(setup)

inline fun button(setup: Button.() -> Unit) = Button().also(setup)

object MenuManager : Listener {

    private val handleMap = hashMapOf<UUID, Openable>() // player uuid to selection
    private val menuStacks = hashMapOf<UUID, Stack<Selection>>() // player uuid to openable history
    val reconnectMap = hashMapOf<UUID, Reconnect>() // player uuid to selection

    private inline fun <reified T> handler(
        channel: String,
        crossinline accept: (T, Player, ByteBuf) -> Unit
    ) where T : Openable =
        Anime.createReader(channel) { player, buffer ->
            // Достаем меню из мапы
            val menu = handleMap[player.uniqueId]
            // Проверяем онлайн ли игрок, есть ли это меню и того ли типа, это меню
            if (!player.isOnline || menu == null || menu !is T)
                return@createReader
            try {
                // Получаем UUID меню
                val uuid = UUID.fromString(NetUtil.readUtf8(buffer))
                // Проверяем полученный UUID с известным
                if (uuid != menu.uuid) {
                    warn("Server side menu uuid verify error! Cheater: ${player.name}")
                    return@createReader
                }
                // Вызываем обработчик данного меню
                accept(menu, player, buffer)
            } catch (exception: Throwable) {
                warn("Player ${player.name} wrote wrong selection uuid!")
            }
        }

    init {
        // Меню выбора
        handler<Selection>("storage:click") { menu, player, buffer ->
            val index = buffer.readInt()
            val button = menu.storage?.get(index) ?: return@handler
            button.onClick?.handle(player, index, button)
        }

        // Меню подтверждения
        handler<Confirmation>("func:accept") { menu, player, _ ->
            menu.onAccept.accept(player)
        }

        // Обработка нажатия на кнопку в меню реконнекта
        Anime.createReader("func:reconnect") { player, _ ->
            reconnectMap[player.uniqueId]?.onClick?.accept(player)
        }

        // Обработка кнопки назад в меню выбора
        Anime.createReader("func:back") { player, _ ->
            menuStacks[player.uniqueId]?.let { stack ->
                stack.pop()
                val menu = stack.peek()
                if (menu is Selection) handleMap[player.uniqueId] = menu
            }
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        handleMap.remove(player.uniqueId)
        menuStacks.remove(player.uniqueId)
        reconnectMap.remove(player.uniqueId)
    }

    @JvmStatic
    fun Openable.open(player: Player, channel: String, transfer: ModTransfer) = apply open@{
        handleMap[player.uniqueId] = this
        transfer.send(channel, player)
    }

    @JvmStatic
    fun pushSelection(player: Player, selection: Selection): Selection = (menuStacks[player.uniqueId] ?: Stack()).apply {
        if (size > 10) {
            warn("Menu history stack is huge! Emergency clearing, player: ${player.name}")
            clearHistory(player)
            return@apply
        }
        menuStacks[player.uniqueId] = this
    }.push(selection)

    @JvmStatic
    fun clearHistory(player: Player) {
        menuStacks[player.uniqueId]?.clear()
    }
}