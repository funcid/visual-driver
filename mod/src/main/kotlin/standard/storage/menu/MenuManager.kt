package standard.storage.menu

import Main.Companion.menuStack
import asColor
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import formatPriceText
import io.netty.buffer.ByteBuf
import org.lwjgl.input.Mouse
import readColoredUtf8
import readRgb
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import standard.storage.AbstractMenu
import standard.storage.Information
import standard.storage.button.ButtonReader.readIcons
import standard.storage.button.StorageItemStack
import standard.storage.button.StorageItemTexture
import standard.storage.menu.selection.SelectionManager
import java.util.*

class MenuManager {

    companion object {

        @Volatile
        var openTimeAndDelayMillis = 0L // Время последнего открытия меню
        val beforeClickDelay = 200 // задержка перед кликом в миллисекундах
        val itemPadding = 4.0

        fun isMenuClickBlocked() = openTimeAndDelayMillis + beforeClickDelay > System.currentTimeMillis()

        fun push(gui: AbstractMenu) {
            menuStack.push(gui)
            openTimeAndDelayMillis = System.currentTimeMillis()
            gui.open()
        }

        init {
            println("Menu manager loaded successfully!")

            // Инициализируем менеджера для Selection
            SelectionManager.run()
            SelectionManager.Companion

            // Открыть меню подтверждения
            mod.registerChannel("func:accept") {
                menuStack.clear()
                Confirmation(UUID.fromString(NetUtil.readUtf8(this)), NetUtil.readUtf8(this)).open()
            }

            // Обновить кнопку
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
                        node.title = readColoredUtf8()

                        if (!inited) return@registerChannel
                        node.titleElement?.content = node.title
                    }

                    3 -> {
                        node.description = readColoredUtf8()

                        if (!inited) return@registerChannel
                        node.descriptionElement?.content = node.description

                        if (last is PlayChoice) return@registerChannel
                        node.optimizeSpace()
                    }

                    4 -> {
                        node.hint = readColoredUtf8()
                        if (!inited) return@registerChannel
                        node.hintElement?.content = node.hint!!
                    }

                    5 -> {
                        if (!inited) return@registerChannel
                        node.hoverText = readColoredUtf8()
                    }

                    6 -> {
                        node.backgroundColor = readRgb()
                        if (!inited) return@registerChannel
                        node.hintContainer?.color = node.backgroundColor.asColor(0.0)
                        node.bundle?.color = node.backgroundColor.asColor(0.28)
                    }

                    7 -> {
                        node.enabled = readBoolean()
                        if (!inited) return@registerChannel
                        node.bundle?.enabled = node.enabled
                    }

                    8 -> {
                        node.price = readLong()
                        if (!inited) return@registerChannel
                        node.priceElement?.updateTitle(formatPriceText(node.sale, node.price, node.priceText))
                    }

                    9 -> {
                        node.priceText = readColoredUtf8()
                        if (!inited) return@registerChannel
                        node.priceElement?.updateTitle(formatPriceText(node.sale, node.price, node.priceText))
                    }

                    else -> return@registerChannel
                }
            }

            // Открыть меню выбора режима
            mod.registerChannel("storage:choice") {
                push(
                    PlayChoice(
                        uuid = UUID.fromString(NetUtil.readUtf8(this)),
                        info = readColoredUtf8(),
                        title = readColoredUtf8(),
                        description = readColoredUtf8(),
                        allowClosing = readBoolean(),
                        storage = readIcons(this)
                    ).apply { open() }
                )
            }

            // Обновление места нахождения текста при наведении
            var lastText: String?

            UIEngine.postOverlayContext.afterRender {
                if (menuStack.isEmpty()) {
                    return@afterRender
                }

                lastText = null
                val stack = menuStack.peek() ?: return@afterRender
                for (node in stack.storage) {
                    if (node.bundle?.hovered == true) {
                        lastText = node.hoverText
                        break
                    }
                }

                // Информационный блок
                if (stack is Information && stack.info.isNotEmpty() && stack.getInformationBlock().hovered) {
                    lastText = stack.info
                }

                if (lastText.isNullOrEmpty()) {
                    return@afterRender
                }

                val resolution = UIEngine.clientApi.resolution()
                val scaleFactor = resolution.scaleFactor

                val x = Mouse.getX() / scaleFactor
                val y = resolution.scaledHeight - Mouse.getY() / scaleFactor

                val screen = UIEngine.clientApi.minecraft().currentScreen()
                screen?.drawHoveringText(lastText!!.split("\n"), x, y)
            }
        }
    }
}
