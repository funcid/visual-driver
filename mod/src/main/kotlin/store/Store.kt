package store

import dev.xdark.feder.NetUtil
import store.signage.SignageScreen
import store.signage.button
import store.util.item
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

const val MARGIN = 4.0

class Store : KotlinMod() {

    lateinit var signageScreen: SignageScreen

    override fun onEnable() {
        UIEngine.initialize(this)

        // Создание магазина, в котором определённое количество кнопок.
        registerChannel("store:make") {
            signageScreen = SignageScreen(*MutableList(readInt()) {
                button {
                    title.content = NetUtil.readUtf8(this@registerChannel)
                    icon.stack = item(NetUtil.readUtf8(this@registerChannel))
                }
            }.toTypedArray())
        }
    }
}
