package standard

import standard.alert.*
import standard.daily.RewardManager
import standard.ui.*
import standard.util.ModelBlocker
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
    }
}
