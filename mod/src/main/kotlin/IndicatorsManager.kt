import dev.xdark.clientapi.event.Cancellable
import dev.xdark.clientapi.event.Event
import dev.xdark.clientapi.event.render.*
import me.func.protocol.Indicators
import ru.cristalix.clientapi.mod
import ru.cristalix.clientapi.registerHandler

object IndicatorsManager {

    private val states = Indicators.values()
        .associateWith { false }
        .toMutableMap()

    init {
        changeIt("func:hide-it", true)
        changeIt("func:show-it", false)

        register<HealthRender>(Indicators.HEALTH)
        register<ExpBarRender>(Indicators.EXP)
        register<HungerRender>(Indicators.HUNGER)
        register<PlayerListRender>(Indicators.TAB)
        register<ArmorRender>(Indicators.ARMOR)
        register<HotbarRender>(Indicators.HOTBAR)
        register<VehicleHealthRender>(Indicators.VEHICLE)
    }

    private inline fun <reified T> register(indicator: Indicators) where T : Event, T : Cancellable {
        registerHandler<T> {
            if (states[indicator] == true)
                isCancelled = true
        }
    }

    private fun changeIt(channel: String, value: Boolean) {
        App::class.mod.registerChannel(channel) {
            val current = Indicators.values()[readInt()]
            states[current] = value
        }
    }

}