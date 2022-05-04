import battlepass.BattlePass
import chat.ChatMod
import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import dialog.DialogMod
import experimental.Experimental
import healthbar.Healthbar
import lootbox.LootboxMod
import npc.NPC
import standard.Standard
import store.Store

class Main : ModMain {
    companion object {
        val standard = Standard()
        val experimental = Experimental()
        val dialog = DialogMod()
        val battlepass = BattlePass()
        val lootbox = LootboxMod()
        val npc = NPC()
        val chat = ChatMod()
        val healthbar = Healthbar()
        val store = Store()
    }

    override fun load(api: ClientApi) {
        standard.load(api)
        experimental.load(api)
        dialog.load(api)
        battlepass.load(api)
        lootbox.load(api)
        npc.load(api)
        chat.load(api)
        healthbar.load(api)
        store.load(api)
    }

    override fun unload() {
        standard.unload()
        experimental.unload()
        dialog.unload()
        battlepass.unload()
        lootbox.unload()
        npc.unload()
        chat.unload()
        healthbar.unload()
        store.unload()
    }
}
