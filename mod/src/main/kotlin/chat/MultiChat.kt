package chat

import dev.xdark.clientapi.event.chat.ChatReceive
import dev.xdark.clientapi.event.chat.ChatSend
import dev.xdark.clientapi.event.gui.ScreenDisplay
import dev.xdark.clientapi.gui.ChatOverlay
import dev.xdark.clientapi.gui.ingame.ChatScreen
import dev.xdark.clientapi.text.Text
import dev.xdark.clientapi.text.TextJSON
import implario.humanize.Humanize
import io.netty.buffer.Unpooled
import org.lwjgl.input.Mouse
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.clientapi.writeUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.onMouseDown
import ru.cristalix.uiengine.utility.*
import java.util.*

class MultiChat {
    private var hoveringText: List<String>? = null
    private val chats: MutableList<Chat> = mutableListOf()
    private var currentChat: Chat? = null

    private val activeColor = Color(42, 102, 189, 1.0)
    private val nonActiveColor = Color(alpha = 0.68)

    init {
        UIEngine.postOverlayContext.afterRender {
            if (hoveringText != null) {
                val resolution = UIEngine.clientApi.resolution()
                val scaleFactor = resolution.scaleFactor

                UIEngine.clientApi.minecraft().currentScreen()?.drawHoveringText(
                    hoveringText,
                    Mouse.getX() / scaleFactor,
                    resolution.scaledHeight - Mouse.getY() / scaleFactor
                )
            }
        }

        createChat(null, "Общий чат", "О", UIEngine.clientApi.minecraft().ingameUI.defaultChatOverlay)
        setCurrentChat(chats[0])

        mod.registerChannel("multichat:message") {
            val chatId = UUID.fromString(readUtf8(36))
            val text: String = readUtf8()
            handleMessage(chats.find { it.id == chatId }!!, TextJSON.jsonToText(text))
        }

        mod.registerChannel("multichat:create") {
            val id = UUID.fromString(readUtf8(36))
            val name = readUtf8()
            val symbol = readUtf8()
            createChat(id, name, symbol)
        }

        mod.registerChannel("multichat:remove") {
            val id = UUID.fromString(readUtf8(36))
            removeChat(id)
        }

        registerHandler<ScreenDisplay> {
            var offsetX = 5.0
            val offsetY = if (screen is ChatScreen) -20.0 else -3.0
            chats.forEach {
                val button = it.button
                button.offset.x = offsetX
                button.offset.y = offsetY
                offsetX += button.size.x + 5.0
            }
        }

        registerHandler<ChatReceive> {
            isCancelled = true
            handleMessage(chats[0], text)
        }

        registerHandler<ChatSend> {
            if (currentChat?.id != chats[0].id) {
                isCancelled = true

                val buf = Unpooled.buffer()
                buf.writeUtf8(currentChat?.id.toString())
                buf.writeUtf8(message)
                UIEngine.clientApi.clientConnection().sendPayload("multichat:message", buf)
            }
        }
    }

    private fun handleMessage(chat: Chat, message: Text) {
        chat.apply {
            if (currentChat != this) {
                unseen++
                val content = "$symbol §c+$unseen"
                (button.children[3] as TextElement).content = content
                button.size.x = UIEngine.clientApi.fontRenderer().getStringWidth(content) * 1.3
            }
            overlay.printText(message)
        }

        var offsetX = 5.0
        chats.forEach {
            val button = it.button
            button.offset.x = offsetX
            offsetX += button.size.x + 5.0
        }
    }

    private fun createChat(id: UUID?, name: String, symbol: String, overlay: ChatOverlay) {
        val symbolSize = if (symbol.length == 1) 15.0 else UIEngine.clientApi.fontRenderer().getStringWidth(symbol) * 1.3

        val chat = Chat(
            id,
            name,
            symbol,
            overlay,
            carved {
                align = BOTTOM_LEFT
                origin = BOTTOM_LEFT

                var offsetX = 5.0
                chats.forEach {
                    offsetX += it.button.size.x + 5.0
                }
                offset.y = -3.0
                offset.x = offsetX

                size = V3(symbolSize, 15.0)
            }
        )

        val button = chat.button

        button.color = nonActiveColor

        val name = text {
            origin = CENTER
            align = CENTER
            content = chat.symbol
            shadow = true
        }

        button.onMouseDown {
            setCurrentChat(chat)
        }

        button.onHover {
            val list: MutableList<String> = mutableListOf(chat.name)
            val unseen = chat.unseen
            if (unseen > 0) {
                list.add("")
                list.add(
                    "§eУ вас $unseen ${
                        Humanize.plurals(
                            "новое",
                            "новых",
                            "новых",
                            unseen
                        )
                    } ${
                        Humanize.plurals(
                            "сообщение",
                            "сообщения",
                            "сообщений",
                            unseen
                        )
                    }!"
                )
            }
            hoveringText = if (hovered) list else null
        }

        button.addChild(name)
        UIEngine.overlayContext.addChild(button)

        chats.add(chat)
    }

    private fun createChat(id: UUID?, name: String, symbol: String) {
        createChat(
            id,
            name,
            symbol,
            ChatOverlay.Builder.builder().minecraft(UIEngine.clientApi.minecraft()).build()
        )
    }

    private fun removeChat(id: UUID) {
        val chat = chats.find { it.id == id }
        if (chat != null) {
            UIEngine.overlayContext.removeChild(chat.button)

            if (chat == currentChat) {
                currentChat = chats[0]
                setCurrentChat(chats[0])
            }

            chats.remove(chat)

            var offsetX = 5.0
            chats.forEach {
                val button = it.button
                button.offset?.x = offsetX
                offsetX += button.size.x + 5.0
            }
        }
    }

    private fun setCurrentChat(chat: Chat) {
        val symbolSize = if (chat.symbol.length == 1) 15.0 else UIEngine.clientApi.fontRenderer().getStringWidth(chat.symbol) * 1.3

        currentChat?.button?.color = nonActiveColor
        currentChat = chat
        chat.button?.color = activeColor

        UIEngine.clientApi.minecraft().ingameUI.chatOverlay = chat.overlay

        chat.unseen = 0
        (chat.button.children[3] as TextElement).content = chat.symbol

        chat.button.size?.x = symbolSize

        var offsetX = 5.0
        chats.forEach {
            val button = it.button
            button.offset?.x = offsetX
            offsetX += button.size.x + 5.0
        }
    }

    data class Chat(
        val id: UUID?,
        val name: String,
        val symbol: String,
        val overlay: ChatOverlay,
        var button: CarvedRectangle,
        var unseen: Int = 0
    )
}