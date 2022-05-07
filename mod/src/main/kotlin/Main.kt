import battlepass.BattlePass
import chat.ChatMod
import dev.xdark.clientapi.event.chat.ChatSend
import dev.xdark.clientapi.text.Text
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
import sun.security.jgss.GSSToken.readInt
import java.awt.SystemColor.text

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


        registerChannel("anime:debug") {
            val fields = readInt()

            fun exists(int: Int, text: String) {
                if (fields > int) clientApi.chat().printChatMessage("§7" + text + ": §b" + NetUtil.readUtf8(this))
            }

            clientApi.chat().printChatMessage("§bi §fAnimation-API успешно работает! §n§bhttps://github.com/cristalix-arcades/animation-api-docs")
            exists(0, "API Version")
            exists(1, "Standards Mods")
            exists(2, "Custom Mods")
            exists(3, "Storage Mods")
            exists(4, "Allocated Mods")
        }

        registerHandler<ChatSend> {
            if (message.startsWith("/func:debug")) {
                clientApi.clientConnection().sendPayload("anime:debug", Unpooled.EMPTY_BUFFER)
                isCancelled = true
            }
        }
    }
}
