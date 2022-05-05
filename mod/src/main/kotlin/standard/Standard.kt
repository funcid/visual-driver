package standard

import Mod
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

const val NAMESPACE = "cache/animation"

context(KotlinMod)
class Standard : Mod {
    override fun load() {
        IndicatorsManager()
        MarkerManager()
        ExternalManager()
        ModelBlocker()
        KillBoardManager()
        SphereManager()
        Title()
        Alert()
        TimeBar()
        GlowEffect()
        RewardManager()
        ItemTitle()
        CorpseManager()
        ScreenAlert()
        CursorAlert()
        RightBottom()
        Ending()
        BigAlert()
    }
}
