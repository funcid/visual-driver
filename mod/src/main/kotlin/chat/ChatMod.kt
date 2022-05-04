package chat

import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

class ChatMod : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        ChatManager
    }
}