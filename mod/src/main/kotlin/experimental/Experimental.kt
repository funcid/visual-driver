package experimental

import experimental.linepointer.LineController
import experimental.linepointer.LineManager
import experimental.panel.Panel
import experimental.places.PlaceController
import experimental.places.PlaceManager
import experimental.progress.ProgressController
import experimental.utils.Test
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
            LineController
            LineManager
            PlaceController
            PlaceManager
            Test()
            Test

            return null
        }
    }
}
