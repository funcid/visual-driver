import battlepass.BattlePass
import chat.ChatMod
import dev.xdark.clientapi.event.chat.ChatSend
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.gui.ingame.AdvancementsScreen
import dev.xdark.clientapi.gui.ingame.OptionsScreen
import dev.xdark.feder.NetUtil
import dialog.DialogMod
import experimental.Experimental
import experimental.storage.Storable
import healthbar.Healthbar
import io.netty.buffer.Unpooled
import lootbox.LootboxMod
import me.func.protocol.Mod
import npc.NPC
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import standard.ExternalManager
import standard.Standard
import java.util.Stack

class Main : KotlinMod() {
    companion object {
        lateinit var externalManager: ExternalManager
        var menuStack: Stack<Storable> = Stack()
    }

    override fun onEnable() {
        UIEngine.initialize(this)

        registerChannel("anime:loadmod") {
            repeat(readInt() /* Count */) {
                when (Mod.values()[readInt() /* Mod Ordinal */]) {
                    Mod.STANDARD -> Standard()
                    Mod.EXPERIMENTAL -> Experimental()
                    Mod.NPC -> NPC()
                    Mod.HEALTHBAR -> Healthbar()
                    Mod.BATTLEPASS -> BattlePass()
                    Mod.LOOTBOX -> LootboxMod()
                    Mod.DIALOG -> DialogMod()
                    Mod.CHAT -> ChatMod()
                    else -> return@repeat
                }
            }
        }

        externalManager = ExternalManager()

        val apiPrefix = "§a§lAPI §7"

        fun message(message: String) = clientApi.chat().printChatMessage(apiPrefix + message)

        registerChannel("anime:debug") {
            val fields = readInt()
            repeat(6) { if (fields > it) message(NetUtil.readUtf8(this).replace(": ", ": §b")) }
        }

        registerChannel("func:close") {
            val mc = UIEngine.clientApi.minecraft()
            val screen = mc.currentScreen()
            if (screen is AdvancementsScreen || screen is OptionsScreen)
                return@registerChannel
            menuStack.clear()
            UIEngine.clientApi.minecraft().displayScreen(null)
        }

        var debugChannels = false

        registerHandler<ChatSend> {
            if (message.startsWith("/func:debug")) {
                clientApi.clientConnection().sendPayload("anime:debug", Unpooled.EMPTY_BUFFER)
                isCancelled = true
            } else if (message.startsWith("/func:channels")) {
                debugChannels = !debugChannels
                message("Статус отладки каналов: $debugChannels")
                isCancelled = true
            }
        }

        registerHandler<PluginMessage> {
            if (debugChannels) message("Канал: §b$channel§7, размер сообщения §b${data.readableBytes()} §7байт.")
        }
    }
}
