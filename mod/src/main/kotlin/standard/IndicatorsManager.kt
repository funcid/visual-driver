package standard

import dev.xdark.clientapi.event.Cancellable
import dev.xdark.clientapi.event.Event
import dev.xdark.clientapi.event.render.AirBarRender
import dev.xdark.clientapi.event.render.ArmorRender
import dev.xdark.clientapi.event.render.ExpBarRender
import dev.xdark.clientapi.event.render.HandRender
import dev.xdark.clientapi.event.render.HealthRender
import dev.xdark.clientapi.event.render.HotbarRender
import dev.xdark.clientapi.event.render.HungerRender
import dev.xdark.clientapi.event.render.NameTemplateRender
import dev.xdark.clientapi.event.render.PlayerListRender
import dev.xdark.clientapi.event.render.PotionsRender
import dev.xdark.clientapi.event.render.VehicleHealthRender
import me.func.protocol.Indicators

import ru.cristalix.clientapi.KotlinMod

context(KotlinMod)
class IndicatorsManager {

    private val states = Indicators.values()
        .associateWith { false }
        .toMutableMap()

    init {
        changeIt("func:hide-it", true)
        changeIt("func:show-it", false)

        register<HealthRender>(Indicators.HEALTH)
        register<ExpBarRender>(Indicators.EXP)
        register<HungerRender>(Indicators.HUNGER)
        register<ArmorRender>(Indicators.ARMOR)
        register<VehicleHealthRender>(Indicators.VEHICLE)
        register<PlayerListRender>(Indicators.TAB)
        register<HotbarRender>(Indicators.HOT_BAR)
        register<AirBarRender>(Indicators.AIR_BAR)
        register<PotionsRender>(Indicators.POTIONS)
        register<HandRender>(Indicators.HAND)
        register<NameTemplateRender>(Indicators.NAME_TEMPLATE)
    }

    private inline fun <reified T> register(indicator: Indicators)
        where T : Event,
              T : Cancellable {
        registerHandler<T> { if (states[indicator] == true) isCancelled = true }
    }

    private fun changeIt(channel: String, value: Boolean) {
        registerChannel(channel) { states[Indicators.values()[readInt()]] = value }
    }
}
