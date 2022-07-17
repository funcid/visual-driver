package experimental

import Main.Companion.menuStack
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import experimental.storage.*
import io.netty.buffer.ByteBuf
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import java.lang.invoke.MutableCallSite
import java.util.*

// не пытайтесь это "оптимизировать", иначе вы все сломаете
class Experimental {
    companion object {
        fun bruh(): Class<*>? {
            Banners()
            Banners.Companion
            GlowPlaces()
            GlowPlaces.Companion
            Recharge()
            Recharge.Companion
            Disguise()
            Disguise.Companion
            Reconnect()
            Reconnect.Companion
            QueueStatus()
            QueueStatus.Companion

            mod.registerChannel("func:accept") {
                menuStack.clear()
                Confirmation(UUID.fromString(NetUtil.readUtf8(this)), NetUtil.readUtf8(this)).open()
            }

            mod.registerChannel("button:update") {
                val last = if (menuStack.size < 1) return@registerChannel else menuStack.peek()
                val index = readInt()
                if (index < 0 || index >= last.storage.size) return@registerChannel
                val node = last.storage[index]
                val inited = node.bundle != null

                when (readByte().toInt()) {
                    0 -> {
                        if (!inited) return@registerChannel
                        val parts = NetUtil.readUtf8(this).split(":", limit = 2)
                        (node as StorageItemTexture).icon.textureLocation = ResourceLocation.of(parts.first(), parts.last())
                    }
                    1 -> {
                        if (!inited) return@registerChannel
                        (node as StorageItemStack).icon.stack = ItemTools.read(this)
                    }
                    2 -> {
                        node.title = NetUtil.readUtf8(this).replace("&", "§")
                        if (!inited) return@registerChannel
                        node.titleElement?.content = node.title
                    }
                    3 -> {
                        node.description = NetUtil.readUtf8(this).replace("&", "§")
                        if (last is PlayChoice || !inited) return@registerChannel
                        node.optimizeSpace()
                    }
                    4 -> {
                        node.hint = NetUtil.readUtf8(this).replace("&", "§")
                        if (!inited) return@registerChannel
                        node.hintElement?.content = node.hint!!
                    }
                    5 -> {
                        if (!inited) return@registerChannel
                        node.hoverText = NetUtil.readUtf8(this).replace("&", "§")
                    }
                    else -> return@registerChannel
                }
            }

            fun readIcons(buffer: ByteBuf): MutableList<StorageNode<*>> = MutableList(buffer.readInt()) {
                if (buffer.readBoolean()) { // real item
                    StorageItemStack(
                        ItemTools.read(buffer), // item
                        buffer.readLong(), // price
                        NetUtil.readUtf8(buffer).replace("&", "§"), // item title
                        NetUtil.readUtf8(buffer).replace("&", "§"), // item description
                        NetUtil.readUtf8(buffer).replace("&", "§"), // item hover desc
                    )
                } else { // texture
                    StorageItemTexture(
                        NetUtil.readUtf8(buffer), // texture
                        buffer.readLong(), // price
                        NetUtil.readUtf8(buffer).replace("&", "§"), // item title
                        NetUtil.readUtf8(buffer).replace("&", "§"), // item description
                        NetUtil.readUtf8(buffer).replace("&", "§"), // item hover desc
                    )
                }
            }

            mod.registerChannel("storage:choice") {
                push(
                    PlayChoice(
                        UUID.fromString(NetUtil.readUtf8(this)),
                        NetUtil.readUtf8(this).replace("&", "§"), // title
                        NetUtil.readUtf8(this).replace("&", "§"), // description
                        readIcons(this)
                    )
                )
            }

            mod.registerChannel("storage:open") {
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

            mod.registerHandler<GameLoop> {
                if (menuStack.isEmpty()) return@registerHandler
                val peek = menuStack.peek()
                if (peek !is StorageMenu) return@registerHandler
                if (!peek.hoverContainer.enabled) return@registerHandler

                val scale = UIEngine.clientApi.resolution().scaleFactor
                peek.hoverContainer.run {
                    offset.x = Mouse.getX() / scale + 6.0
                    offset.y = (Display.getHeight() - Mouse.getY()) / scale - 12.0
                }
            }
            return null
        }

        private fun push(gui: Storable) {
            if (menuStack.size > 20)
                menuStack.clear()
            menuStack.push(gui)
            gui.open()
        }
    }
}
