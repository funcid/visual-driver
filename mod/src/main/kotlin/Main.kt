import battlepass.BattlePass
import chat.ChatMod
import dev.xdark.clientapi.event.chat.ChatSend
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.feder.NetUtil
import dialog.DialogMod
import experimental.Experimental
import healthbar.Healthbar
import io.netty.buffer.Unpooled
import lootbox.LootboxMod
import me.func.protocol.Mod
import npc.NPC
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import standard.Standard
import store.Store

class Main : KotlinMod() {
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
                    Mod.STORE -> Store()
                }
            }
        }

        val apiPrefix = "§a§lAPI §7"

        fun message(message: String) = clientApi.chat().printChatMessage(apiPrefix + message)

        registerChannel("anime:debug") {
            val fields = readInt()
            repeat(6) { if (fields > it) message(NetUtil.readUtf8(this).replace(": ", ": §b")) }
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
