import battlepass.BattlePass
import chat.ChatMod
import dialog.DialogMod
import experimental.Experimental
import healthbar.Healthbar
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
    }
}
