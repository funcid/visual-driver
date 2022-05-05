package chat

import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

class ChatMod : KotlinMod() {

    companion object {
        lateinit var mod: ChatMod
            private set
    }

    override fun onEnable() {
        UIEngine.initialize(this)

        mod = this

        ChatManager
    }
}