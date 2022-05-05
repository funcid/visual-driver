package experimental

import Mod
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

context(KotlinMod)
class Experimental : Mod {
    override fun load() {
        Banners()
        GlowPlaces()
        Recharge()
        Disguise()
    }
}
