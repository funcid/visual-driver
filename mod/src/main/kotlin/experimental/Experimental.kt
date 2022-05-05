package experimental

import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

class Experimental : KotlinMod() {

    companion object {
        lateinit var mod: Experimental
            private set
    }

    override fun onEnable() {
        UIEngine.initialize(this)

        mod = this

        GlowPlaces
        Banners
        Recharge
        Disguise
    }
}