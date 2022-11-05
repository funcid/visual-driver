package experimental

import experimental.panel.Panel
import experimental.progress.ProgressController
import experimental.progress.AbstractProgress
import standard.ui.scoreboard.token.TokenManager

// не пытайтесь это "оптимизировать", иначе вы все сломаете
class Experimental {
    companion object {

        fun load(): Class<*>? {
            println("Experimental module loaded successfully!")

            GlowPlaces()
            GlowPlaces.Companion
            Disguise()
            Disguise.Companion
            ProgressController()
            ProgressController.Companion
            Panel()
            TokenManager
            return null
        }
    }
}
