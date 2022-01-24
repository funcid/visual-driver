import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

const val NAMESPACE = "cache/animation"

class App : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        MarkerManager
        ExternalManager
        ModelBlocker
        KillBoardManager
        Title
        Alert
        TimeBar
        GlowEffect
        IndicatorsManager
        RewardManager
        ItemTitle
        CorpseManager
        ScreenAlert
        CursorAlert
        RightBottom
        Ending
    }
}