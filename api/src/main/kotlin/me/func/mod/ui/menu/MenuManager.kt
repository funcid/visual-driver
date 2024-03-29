package me.func.mod.ui.menu

import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import me.func.mod.Anime
import me.func.mod.Anime.provided
import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.mod.conversation.data.MouseButton
import me.func.mod.reactive.ReactiveButton
import me.func.mod.ui.menu.choicer.Choicer
import me.func.mod.ui.menu.confirmation.Confirmation
import me.func.mod.ui.menu.daily.DailyRewardMenu
import me.func.mod.ui.menu.recconnct.Reconnect
import me.func.mod.ui.menu.selection.Selection
import me.func.mod.util.warn
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

inline fun selection(setup: Selection.() -> Unit) = Selection().also(setup)

inline fun dailyReward(setup: DailyRewardMenu.() -> Unit) = DailyRewardMenu().also(setup)

inline fun choicer(setup: Choicer.() -> Unit) = Choicer().also(setup)

inline fun button(setup: ReactiveButton.() -> Unit) = ReactiveButton().also(setup)

object MenuManager : PlayerSubscriber {

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
                warn("Player ${player.name} wrote wrong selection uuid! " + exception.stackTraceToString())
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
            val page = buffer.readInt()
            if (!menu.isPageExists(page)) return@handler
            menu.sendPage(page, player)
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

                if (stack.empty()) return@let

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

    @JvmStatic
    fun Openable.bind(player: Player): ModTransfer {
        handleMap[player.uniqueId] = this
        return ModTransfer().string(uuid.toString())
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Storage> push(player: Player, storage: T) = (menuStacks[player.uniqueId] ?: Stack()).apply {
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

    fun ReactiveButton.reactive(setup: ModTransfer.() -> Unit) =
        menuStacks.filter { !it.value.empty() && it.value.peek().storage.contains(this) }.forEach { (uuid, stack) ->
            ModTransfer().integer(stack.peek().storage.indexOf(this)).also(setup)
                .send("button:update", Bukkit.getPlayer(uuid) ?: return@forEach)
        }

    fun getAllViewers(menu: Openable): List<Player> =
        menuStacks
            .filter { it.value.isNotEmpty() && it.value.peek() == menu }
            .mapNotNull { Bukkit.getPlayer(it.key) }

    override val isConstant = true

    override fun removeSubscriber(player: Player) {
        handleMap.remove(player.uniqueId)
        menuStacks.remove(player.uniqueId)
        reconnectMap.remove(player.uniqueId)
    }

    override fun getSubscribersCount() = handleMap.size
    override val uuid: UUID = UUID.randomUUID()
}
