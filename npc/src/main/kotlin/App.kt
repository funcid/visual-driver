import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

class App: KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)
    }

}