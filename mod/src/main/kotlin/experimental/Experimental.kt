package experimental

import dev.xdark.clientapi.item.ItemTools
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import experimental.storage.*
import io.netty.buffer.ByteBuf
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.clientapi.KotlinMod
import menuStack
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
            menuStack.clear()
            Confirmation(UUID.fromString(NetUtil.readUtf8(this)), NetUtil.readUtf8(this)).open()
        }

        registerChannel("button:update") {
            val last = if (menuStack.size < 1) return@registerChannel else menuStack.peek()
            val index = readInt()
            if (index < 0 || index >= last.storage.size) return@registerChannel
            val node = last.storage[index]
            if (node.bundle == null) return@registerChannel

            when (readByte().toInt()) {
                0 -> {
                    val parts = NetUtil.readUtf8(this).split(":", limit = 2)
                    (node as StorageItemTexture).icon.textureLocation = ResourceLocation.of(parts.first(), parts.last())
                }
                1 -> (node as StorageItemStack).icon.stack = ItemTools.read(this)
                2 -> {
                    node.title = NetUtil.readUtf8(this).replace("&", "§")
                    node.titleElement?.content = node.title
                }
                3 -> {
                    node.description = NetUtil.readUtf8(this).replace("&", "§")
                    if (last is PlayChoice)
                        return@registerChannel
                    node.optimizeSpace()
                }
                4 -> {
                    node.hint = NetUtil.readUtf8(this).replace("&", "§")
                    node.hintElement?.content = node.hint!!
                }
            }
        }

        fun readIcons(buffer: ByteBuf): MutableList<StorageNode<*>> = MutableList(buffer.readInt()) {
            if (buffer.readBoolean()) { // real item
                StorageItemStack(
                    ItemTools.read(buffer), // item
                    buffer.readLong(), // price
                    NetUtil.readUtf8(buffer).replace("&", "§"), // item title
                    NetUtil.readUtf8(buffer).replace("&", "§"), // item description
                )
            } else { // texture
                StorageItemTexture(
                    NetUtil.readUtf8(buffer), // texture
                    buffer.readLong(), // price
                    NetUtil.readUtf8(buffer).replace("&", "§"), // item title
                    NetUtil.readUtf8(buffer).replace("&", "§"), // item description
                )
            }
        }

        fun push(gui: Storable) {
            if (!menuStack.empty() && menuStack.peek().javaClass != gui.javaClass)
                menuStack.clear()
            menuStack.push(gui)
            gui.open()
        }

        registerChannel("storage:choice") {
            push(
                PlayChoice(
                    UUID.fromString(NetUtil.readUtf8(this)),
                    NetUtil.readUtf8(this).replace("&", "§"), // title
                    NetUtil.readUtf8(this).replace("&", "§"), // description
                    readIcons(this)
                )
            )
        }

        registerChannel("storage:open") {
            push(
                StorageMenu(
                    UUID.fromString(NetUtil.readUtf8(this)),
                    NetUtil.readUtf8(this).replace("&", "§"), // title
                    NetUtil.readUtf8(this), // vault
                    NetUtil.readUtf8(this).replace("&", "§"), // money title
                    NetUtil.readUtf8(this).replace("&", "§"), // hint
                    readInt(), // rows
                    readInt(), // columns
                    readIcons(this)
                )
            )
        }
    }
}
