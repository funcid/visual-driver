import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import java.util.*

class BattlePass : KotlinMod() {

    private val map = mutableMapOf<UUID, BattlePassGui>()

    override fun onEnable() {
        UIEngine.initialize(this)

        registerChannel("bp:send") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val price = readInt()
            val sale = readDouble()

            val tags = MutableList(readInt()) { NetUtil.readUtf8(this) }
            val items = arrayListOf<ItemStack>()
            val advancedItems = arrayListOf<ItemStack>()

            val pages = MutableList(readInt()) {
                val requiredExp = readInt()

                repeat(readInt()) { items.add(ItemTools.read(this)) }
                repeat(readInt()) { advancedItems.add(ItemTools.read(this)) }

                BattlePage(it + 1, requiredExp)
            }

            val quests = MutableList(readInt()) { NetUtil.readUtf8(this) }

            map[uuid] =
                BattlePassGui(tags.joinToString("\n"), price, sale, pages.size, pages, quests, items, advancedItems)
        }
        registerChannel("bp:show") {
            val gui = map[UUID.fromString(NetUtil.readUtf8(this))]!!
            gui.exp = readInt()
            gui.isAdvanced = readBoolean()

            val page = getPage(gui.pages, gui.exp)!!
            gui.requiredExp = page.exp
            gui.level = page.index + 1

            gui.open()
        }

        registerChannel("bp:quests") {
            map[UUID.fromString(NetUtil.readUtf8(this))]?.let {
                it.quests = MutableList(readInt()) { NetUtil.readUtf8(this) }
            }
        }
    }
}