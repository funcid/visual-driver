package standard

import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

const val NAMESPACE = "cache/animation"

class Standard : KotlinMod() {

    companion object {
        lateinit var mod: Standard
            private set
    }

    override fun onEnable() {
        UIEngine.initialize(this)

        mod = this

        IndicatorsManager
        MarkerManager
        ExternalManager
        ModelBlocker
        KillBoardManager
        SphereManager
        Title
        Alert
        TimeBar
        GlowEffect
        RewardManager
        ItemTitle
        CorpseManager
        ScreenAlert
        CursorAlert
        RightBottom
        Ending
        BigAlert
    }
}