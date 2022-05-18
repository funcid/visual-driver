package experimental

import dev.xdark.clientapi.item.ItemTools
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import experimental.storage.*
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.Flex
import selectionStack
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.element.CarvedRectangle
import sun.security.jgss.GSSToken.readInt
import java.util.*

context(KotlinMod)
class Experimental {

    init {
        Banners()
        GlowPlaces()
        Recharge()
        Disguise()
        Reconnect()

        registerChannel("func:accept") {
            selectionStack.clear()
            Confirmation(UUID.fromString(NetUtil.readUtf8(this)), NetUtil.readUtf8(this)).open()
        }

        registerChannel("button:update") {
            val last = selectionStack.peek() ?: return@registerChannel
            val index = readInt()
            if (index < 0 || index >= last.storage.size) return@registerChannel
            val node = last.storage[index]
            if (node.fullElement == null) return@registerChannel

            fun getLore(order: Int) = ((node.fullElement!!.children[1] as Flex).children[order] as TextElement)

            when (readByte().toInt()) {
                 0 -> {
                    val parts = NetUtil.readUtf8(this).split(":", limit = 2)
                    (node as StorageItemTexture).icon.textureLocation = ResourceLocation.of(parts.first(), parts.last())
                }
                1 -> (node as StorageItemStack).icon.stack = ItemTools.read(this)
                2 -> getLore(0).content = NetUtil.readUtf8(this).replace("&", "§")

                3 -> getLore(1).content = NetUtil.readUtf8(this).replace("&", "§")
                4 -> ((node.fullElement!!.children.last() as CarvedRectangle).children.first() as TextElement).content = NetUtil.readUtf8(this).replace("&", "§")
            }
        }

        registerChannel("storage:open") {
            val screen = StorageMenu(
                UUID.fromString(NetUtil.readUtf8(this)),
                NetUtil.readUtf8(this).replace("&", "§"), // title
                NetUtil.readUtf8(this), // vault
                NetUtil.readUtf8(this).replace("&", "§"), // money title
                NetUtil.readUtf8(this).replace("&", "§"), // hint
                readInt(), // rows
                readInt(), // columns
                MutableList(readInt()) { // item count
                    if (readBoolean()) { // real item
                        StorageItemStack(
                            ItemTools.read(this), // item
                            readLong(), // prize
                            NetUtil.readUtf8(this).replace("&", "§"), // item title
                            NetUtil.readUtf8(this).replace("&", "§"), // item description
                        )
                    } else { // texture
                        StorageItemTexture(
                            NetUtil.readUtf8(this), // texture
                            readLong(), // prize
                            NetUtil.readUtf8(this).replace("&", "§"), // item title
                            NetUtil.readUtf8(this).replace("&", "§"), // item description
                        )
                    }
                })
            screen.open()
            selectionStack.push(screen)
        }
    }
}
