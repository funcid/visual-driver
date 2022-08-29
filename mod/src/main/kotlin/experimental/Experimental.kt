package experimental

import Main.Companion.menuStack
import dev.xdark.clientapi.event.gui.ScreenDisplay
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import experimental.storage.*
import io.netty.buffer.ByteBuf
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.utility.*
import java.util.*

// не пытайтесь это "оптимизировать", иначе вы все сломаете
class Experimental {
    companion object {

        var openTimeAndDelayMillis = 0L // Время последнего открытия меню
        val beforeClickDelay = 200 // задержка перед кликом в миллисекундах
        val itemPadding = 4.0
        val hoverTextScale = 0.5 + 0.25 + 0.125

        fun isMenuClickBlocked() = openTimeAndDelayMillis + beforeClickDelay > System.currentTimeMillis()

        val hoverText = text {
            shadow = true
            lineHeight += 2
            scale = V3(0.75, 0.75, 0.75)
            color = WHITE
            offset = V3(itemPadding, itemPadding)
        }
        val hoverCenter = carved {
            color = Color(42, 102, 189, 1.0)
            offset = V3(1.0, 1.0)
            +hoverText
        }

        val hoverContainer = carved {
            color = Color(0, 0, 0, 0.38)
            enabled = false
            +hoverCenter

            beforeRender {
                val scale = UIEngine.clientApi.resolution().scaleFactor
                offset.x = Mouse.getX() / scale + 6.0
                offset.y = (Display.getHeight() - Mouse.getY()) / scale - 12.0
                GlStateManager.disableDepth()
            }
            afterRender {
                GlStateManager.enableDepth()
            }
        }

        var oldHoverText: List<String>? = null

        fun <T> acceptHover(hovered: Boolean, element: StorageNode<T>) where T : AbstractElement {
            if (hovered && element.hoverText.isNotEmpty()) {
                val lines = element.hoverText.split("\n")
                if (element.oldHover) {
                    if (oldHoverText == null) {
                        oldHoverText = lines
                    }
                } else {
                    if (!hoverContainer.enabled) {
                        hoverText.content = element.hoverText
                        hoverContainer.enabled = true
                    }

                    hoverContainer.size.x =
                        UIEngine.clientApi.fontRenderer().getStringWidth(lines.maxByOrNull { it.length } ?: "")
                            .toDouble() * hoverTextScale
                    hoverContainer.size.y = hoverText.lineHeight * lines.count() * hoverTextScale + itemPadding
                    hoverCenter.size.x = hoverContainer.size.x - 2
                    hoverCenter.size.y = hoverContainer.size.y - 2
                }
            } else {
                hoverContainer.enabled = false
                oldHoverText = null
            }
        }

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
                        (node as StorageItemTexture).icon.textureLocation =
                            ResourceLocation.of(parts.first(), parts.last())
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

                    6 -> {
                        if (inited) return@registerChannel
                        node.oldHover = readBoolean()
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
                        buffer.readBoolean(), // item hover type
                        buffer.readBoolean(), // special
                    )
                } else { // texture
                    StorageItemTexture(
                        NetUtil.readUtf8(buffer), // texture
                        buffer.readLong(), // price
                        NetUtil.readUtf8(buffer).replace("&", "§"), // item title
                        NetUtil.readUtf8(buffer).replace("&", "§"), // item description
                        NetUtil.readUtf8(buffer).replace("&", "§"), // item hover desc
                        buffer.readBoolean(), // item hover type
                        buffer.readBoolean(), // special
                    )
                }
            }

            mod.registerChannel("storage:choice") {
                push(
                    PlayChoice(
                        uuid = UUID.fromString(NetUtil.readUtf8(this)),
                        title = NetUtil.readUtf8(this).replace("&", "§"),
                        description = NetUtil.readUtf8(this).replace("&", "§"),
                        allowClosing = readBoolean(),
                        storage = readIcons(this)
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

            UIEngine.postOverlayContext.afterRender {
                oldHoverText?.apply {
                    val resolution = UIEngine.clientApi.resolution()
                    val scaleFactor = resolution.scaleFactor

                    val x = Mouse.getX() / scaleFactor
                    val y = resolution.scaledHeight - Mouse.getY() / scaleFactor

                    val screen = UIEngine.clientApi.minecraft().currentScreen()
                    screen?.drawHoveringText(this, x, y)
                }
            }

            mod.registerHandler<ScreenDisplay> {
                oldHoverText = null
            }

            return null
        }

        private fun push(gui: Storable) {
            if (menuStack.size > 20)
                menuStack.clear()
            menuStack.push(gui)
            openTimeAndDelayMillis = System.currentTimeMillis()
            gui.open()
        }
    }
}
