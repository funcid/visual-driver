import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

const val NAMESPACE = "cache/animation"

class App : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        IndicatorsManager
        MarkerManager
        ExternalManager
        ModelBlocker
        KillBoardManager
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
    }
}