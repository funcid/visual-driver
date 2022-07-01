package standard

import ru.cristalix.clientapi.KotlinMod

const val NAMESPACE = "cache/animation"

context(KotlinMod)
class Standard {
    init {
        IndicatorsManager()
        MarkerManager()
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
