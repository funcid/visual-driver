
import dev.xdark.clientapi.event.chat.ChatReceive
import dev.xdark.clientapi.gui.ChatOverlay
import dev.xdark.clientapi.text.Text
import dev.xdark.clientapi.text.TextJSON
import io.netty.buffer.Unpooled
import ru.cristalix.clientapi.mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.onMouseDown
import ru.cristalix.uiengine.utility.*

object ChatManager {

    private val chats = mutableMapOf(
        1 to createChat("О", "Общий межсерверный чат"),
        2 to createChat("С", "Системный чат"),
        3 to createChat("Б", "Боевой чат"),
        4 to createChat("Г", "Групповой чат"),
        5 to createChat("Т", "Торговый чат"),
    )
    private var currentChat = 1

    init {

        val activeColor = Color(42, 102, 189, 1.0)
        val nonActiveColor = Color(alpha = 0.68)

        UIEngine.clientApi.minecraft().ingameUI.chatOverlay = chats[currentChat]!!.overlay

        var offsetX = 5.0
        chats.forEach { (id, chat) ->
            val button = chat.button
            button.color = if (currentChat == id) activeColor else nonActiveColor
            button.offset.x = offsetX
            offsetX += button.size.x + 5.0

            val name = text {
                origin = CENTER
                align = CENTER
                content = chat.name
                shadow = true
            }

            button.onMouseDown {
                chats[currentChat]?.button?.color = nonActiveColor
                currentChat = id
                chats[id]?.button?.color = activeColor
                UIEngine.clientApi.minecraft().ingameUI.chatOverlay = chat.overlay
                chat.unseen = 0
                name.content = chat.name
                button.size.x = 15.0

                var offsetX = 5.0
                chats.values.forEach {
                    val button = it.button
                    button.offset.x = offsetX
                    offsetX += button.size.x + 5.0
                }

                UIEngine.clientApi.clientConnection()
                    .sendPayload("zabelix:select_chat", Unpooled.copyInt(currentChat))
            }
            button.addChild(name)
            UIEngine.overlayContext.addChild(button)
        }

        App::class.mod.registerChannel("delete-chat") {
            val index = readInt()
            chats[index]!!.button.enabled = false
            chats.remove(index)
        }

        App::class.mod.registerChannel("zabelix:chat_message") {
            handleMessage(readInt(), TextJSON.jsonToText(readUtf8()))
        }

        ru.cristalix.clientapi.registerHandler<ChatReceive> {
            val raw = text.unformattedText
            if (currentChat != 1 && raw.startsWith("[VC]")) {
                isCancelled = true
                chats[1]!!.overlay.printText(text)
            }
        }
    }

    private fun handleMessage(chatId: Int, message: Text) {
        chats[chatId]?.apply {
            if (currentChat != chatId) {
                unseen++
                val content = "$name §c+$unseen"
                (button.children[0] as TextElement).content = content
                button.size.x = UIEngine.clientApi.fontRenderer().getStringWidth(content) * 1.3
            }

            overlay.printText(message)
        }

        var offsetX = 5.0
        chats.values.forEach {
            val button = it.button
            button.offset.x = offsetX
            offsetX += button.size.x + 5.0
        }

        if (chatId == 1) {
            UIEngine.clientApi.minecraft().ingameUI.defaultChatOverlay.printText(message)
        }
    }

    private fun createChat(name: String, hover: String): Chat {
        return Chat(
            name,
            hover,
            ChatOverlay.Builder.builder().minecraft(UIEngine.clientApi.minecraft()).build(),
            rectangle {
                align = BOTTOM_LEFT
                origin = BOTTOM_LEFT
                offset.y = -20.0
                size = V3(15.0, 15.0)
            }
        )
    }

    data class Chat(
        val name: String,
        val hover: String,
        val overlay: ChatOverlay,
        var button: RectangleElement,
        var unseen: Int = 0
    )
}