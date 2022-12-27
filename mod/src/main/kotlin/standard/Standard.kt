package standard

import experimental.Recharge
import standard.alert.*
import standard.storage.menu.daily.RewardManager
import standard.storage.menu.MenuManager
import standard.storage.menu.QueueStatus
import standard.storage.menu.Reconnect
import standard.ui.*
import standard.ui.scoreboard.ScoreBoardManager
import standard.util.ModelBlocker
import standard.world.CorpseManager
import standard.world.MarkerManager
import standard.world.SphereManager
import standard.world.banners.BannersController
import standard.world.banners.BannersManager

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
        ScoreBoardManager()
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
        BannersController
        BannersManager
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
