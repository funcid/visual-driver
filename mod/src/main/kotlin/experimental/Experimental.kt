package experimental

import experimental.progress.ProgressController
import experimental.progress.UIProgress

// не пытайтесь это "оптимизировать", иначе вы все сломаете
class Experimental {
    companion object {

        fun load(): Class<*>? {
            println("Experimental module loaded successfully!")

            GlowPlaces()
            GlowPlaces.Companion
            Disguise()
            Disguise.Companion
            UIProgress()
            ProgressController()
            ProgressController.Companion
            return null
        }
    }
}
