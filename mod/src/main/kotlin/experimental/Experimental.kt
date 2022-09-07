package experimental

import experimental.storage.menu.MenuManager
import experimental.storage.menu.QueueStatus
import experimental.storage.menu.Reconnect

// не пытайтесь это "оптимизировать", иначе вы все сломаете
class Experimental {
    companion object {

        fun load(): Class<*>? {
            println("Experimental module loaded successfully!")

            MenuManager()
            MenuManager.Companion
            Banners()
            Banners.Companion
            GlowPlaces()
            GlowPlaces.Companion
            Recharge()
            Recharge.Companion
            Disguise()
            Disguise.Companion
            Reconnect()
            Reconnect.Companion
            QueueStatus()
            QueueStatus.Companion
            return null
        }
    }
}
