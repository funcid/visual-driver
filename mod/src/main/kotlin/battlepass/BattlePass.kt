package battlepass

import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.KotlinMod
import java.util.UUID

context(KotlinMod)
class BattlePass {
    private val map = mutableMapOf<UUID, BattlePassGui>()

    init {
        registerChannel("bp:send") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val price = readInt()
            val sale = readDouble()

            val tags = MutableList(readInt()) { NetUtil.readUtf8(this) }

            val pages = MutableList(readInt()) {
                val requiredExp = readInt()

                val items = arrayListOf<ItemStack>()
                val advancedItems = arrayListOf<ItemStack>()

                repeat(readInt()) { items.add(ItemTools.read(this)) }
                repeat(readInt()) { advancedItems.add(ItemTools.read(this)) }

                BattlePage(items, advancedItems, 0, requiredExp, readInt())
            }

            val quests = MutableList(readInt()) { NetUtil.readUtf8(this) }

            map[uuid] = BattlePassGui(uuid, tags.joinToString("\n"), price, sale, pages, quests, mutableListOf())
        }

        registerChannel("bp:show") {
            val gui = map[UUID.fromString(NetUtil.readUtf8(this))]!!
            val exp = readInt()
            gui.isAdvanced = readBoolean()

            val (page, level) = getPage(gui.pages, exp)
            gui.requiredExp = page!!.requiredExp
            gui.exp = page.exp
            gui.level = level
            gui.skipPrice = page.skipPrice

            gui.update()
            gui.open()
        }

        registerChannel("bp:quests") {
            map[UUID.fromString(NetUtil.readUtf8(this))]?.let {
                it.quests = MutableList(readInt()) { NetUtil.readUtf8(this) }
                it.update()
            }
        }

        registerChannel("bp:claimed") {
            map[UUID.fromString(NetUtil.readUtf8(this))]?.let {
                it.claimed = MutableList(readInt()) { readInt() }
            }
        }
    }
}
