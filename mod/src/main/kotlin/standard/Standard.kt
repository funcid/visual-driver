package standard

import experimental.Recharge
import standard.alert.*
import standard.daily.RewardManager
import standard.storage.menu.MenuManager
import standard.storage.menu.QueueStatus
import standard.storage.menu.Reconnect
import standard.ui.*
import standard.util.ModelBlocker
import standard.world.Banners
import standard.world.CorpseManager
import standard.world.MarkerManager
import standard.world.SphereManager

const val NAMESPACE = "cache/animation"

class Standard {
    init {
        IndicatorsManager()
        MarkerManager()
        ModelBlocker()
        KillBoard()
        SphereManager()
        Title()
        Alert()
        TimeBar()
        GlowEffect()
        RewardManager()
        ItemTitleAlert()
        CorpseManager()
        ScreenAlert()
        CursorAlert()
        OverlayText()
        EndingAlert()
        BigAlert()
        Boosters()
        SystemMessageAlert()
        Banners()
        MenuManager()
        MenuManager.Companion
        QueueStatus()
        QueueStatus.Companion
        Recharge()
        Recharge.Companion
        Reconnect()
        Reconnect.Companion
    }
}
