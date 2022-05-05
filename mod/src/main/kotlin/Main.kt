import battlepass.BattlePass
import chat.ChatMod
import dialog.DialogMod
import experimental.Experimental
import healthbar.Healthbar
import lootbox.LootboxMod
import npc.NPC
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import standard.Standard
import store.Store

interface Mod {
    fun load()
}

class Main : KotlinMod() {
    private val standard = Standard()
    private val experimental = Experimental()
    private val dialog = DialogMod()
    private val battlepass = BattlePass()
    private val lootbox = LootboxMod()
    private val npc = NPC()
    private val chat = ChatMod()
    private val healthbar = Healthbar()
    private val store = Store()

    override fun onEnable() {
        UIEngine.initialize(this)

        standard.load()
        experimental.load()
        dialog.load()
        battlepass.load()
        lootbox.load()
        npc.load()
        chat.load()
        healthbar.load()
        store.load()
    }
}
