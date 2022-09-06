package me.func.mod.menu

import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import me.func.mod.Anime
import me.func.mod.Anime.provided
import me.func.mod.conversation.ModTransfer
import me.func.mod.menu.choicer.Choicer
import me.func.mod.menu.confirmation.Confirmation
import me.func.mod.menu.recconnct.Reconnect
import me.func.mod.menu.selection.Selection
import me.func.mod.util.MouseButton
import me.func.mod.util.warn
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

inline fun selection(setup: Selection.() -> Unit) = Selection().also(setup)

inline fun choicer(setup: Choicer.() -> Unit) = Choicer().also(setup)

inline fun button(setup: Button.() -> Unit) = Button().also(setup)

object MenuManager : Listener {

    val handleMap = hashMapOf<UUID, Openable>() // player uuid to selection
    val menuStacks = hashMapOf<UUID, Stack<Storage>>() // player uuid to openable history
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
        handler<Storage>("storage:click") { menu, player, buffer ->
            val index = buffer.readInt()
            val button = menu.storage[index]
            when (buffer.readInt()) {
                MouseButton.LEFT.ordinal -> button.onLeftClick?.handle(player, index, button)
                MouseButton.RIGHT.ordinal -> button.onRightClick?.handle(player, index, button)
                MouseButton.MIDDLE.ordinal -> button.onMiddleClick?.handle(player, index, button)
            }
            button.onClick?.handle(player, index, button)
        }

        // Многостраничное меню
        handler<Paginated>("func:page-request") { menu, player, buffer ->
            val index = buffer.readInt()
        }

        // Меню подтверждения - принятие / отказ
        handler<Confirmation>("func:yesno") { menu, player, buffer ->
            if (buffer.readBoolean()) menu.onAccept.accept(player)
            else menu.onDeny?.accept(player)
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
                if (menu is Storage) menu.bind(player)
            }
        }

        // Тикаем все меню
        Bukkit.getScheduler().runTaskTimer(provided, {
            Bukkit.getOnlinePlayers().filter { handleMap.containsKey(it.uniqueId) }.forEach {
                val stack = menuStacks[it.uniqueId] ?: return@forEach
                if (stack.empty()) return@forEach
                val top = stack.peek()
                if (top is Selection) top.tick?.accept(top)
            }
        }, 1, 1)
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        handleMap.remove(player.uniqueId)
        menuStacks.remove(player.uniqueId)
        reconnectMap.remove(player.uniqueId)
    }

    @JvmStatic
    fun Openable.bind(player: Player): ModTransfer {
        handleMap[player.uniqueId] = this
        return ModTransfer().string(uuid.toString())
    }

    @JvmStatic
    fun <T : Storage> T.open(
        player: Player,
        channel: String,
        customStorage: List<Button>? = null,
        transfer: ModTransfer.() -> Unit
    ): T = push(player, this).apply {
        bind(player)
            .string(title)
            .also(transfer)
            .integer((customStorage ?: storage).size)
            .apply { (customStorage ?: storage).forEach { it.write(this) } }
            .send(channel, player)
    }.apply {
        // Отдельно обновляем текст при наведении на кнопки
        (customStorage ?: storage).filter { it.hint != null }
            .forEach { it.reactive { byte(4).string(it.hint!!) } }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Storage> push(player: Player, storage: T) = (menuStacks[player.uniqueId] ?: Stack()).apply {
        if (size > 10) {
            warn("Menu history stack is huge! Emergency clearing, player: ${player.name}")
            clearHistory(player)
            return@apply
        }
        menuStacks[player.uniqueId] = this
    }.push(storage) as T

    @JvmStatic
    fun clearHistory(player: Player) {
        menuStacks[player.uniqueId]?.clear()
    }

    fun Button.reactive(setup: ModTransfer.() -> Unit) =
        menuStacks.filter { !it.value.empty() && it.value.peek().storage.contains(this) }.forEach { (uuid, stack) ->
            ModTransfer().integer(stack.peek().storage.indexOf(this)).also(setup)
                .send("button:update", Bukkit.getPlayer(uuid) ?: return@forEach)
        }
}
