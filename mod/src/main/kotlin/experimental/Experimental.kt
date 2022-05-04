package experimental

import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

class Experimental : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        GlowPlaces
        Banners
        Recharge
        Disguise
    }
}