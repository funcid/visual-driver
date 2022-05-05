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
    private val standard = Standard()
    private val experimental = Experimental()
    private val dialog = DialogMod()
    private val battlepass = BattlePass()
    private val lootbox = LootboxMod()
    private val npc = NPC()
    private val chat = ChatMod()
    private val healthbar = Healthbar()
    private val store = Store()

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
