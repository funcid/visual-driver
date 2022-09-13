package experimental

import experimental.progress.ProgressController
import experimental.progress.AbstractProgress

// не пытайтесь это "оптимизировать", иначе вы все сломаете
class Experimental {
    companion object {

        fun load(): Class<*>? {
            println("Experimental module loaded successfully!")

            GlowPlaces()
            GlowPlaces.Companion
            Disguise()
            Disguise.Companion
            AbstractProgress()
            ProgressController()
            ProgressController.Companion
            return null
        }
    }
}
