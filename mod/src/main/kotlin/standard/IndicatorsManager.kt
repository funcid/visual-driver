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
import ru.cristalix.clientapi.KotlinModHolder
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine.listener

class IndicatorsManager {

    private val states = Indicators.values()
        .associateWith { false }
        .toMutableMap()

    init {
        changeIt("func:hide-it", true)
        changeIt("func:show-it", false)

        register(HealthRender::class.java, Indicators.HEALTH)
        register(ExpBarRender::class.java, Indicators.EXP)
        register(HungerRender::class.java, Indicators.HUNGER)
        register(ArmorRender::class.java, Indicators.ARMOR)
        register(VehicleHealthRender::class.java, Indicators.VEHICLE)
        register(PlayerListRender::class.java, Indicators.TAB)
        register(HotbarRender::class.java, Indicators.HOT_BAR)
        register(AirBarRender::class.java, Indicators.AIR_BAR)
        register(PotionsRender::class.java, Indicators.POTIONS)
        register(HandRender::class.java, Indicators.HAND)
        register(NameTemplateRender::class.java, Indicators.NAME_TEMPLATE)
    }

    private fun <T> register(clazz: Class<T>, indicator: Indicators)
        where T : Event,
              T : Cancellable {
        Event.bus(clazz).register(mod.listener, {
            if (states[indicator] == true) it.isCancelled = true
        }, 1)
    }

    private fun changeIt(channel: String, value: Boolean) {
        mod.registerChannel(channel) { states[Indicators.values()[readInt()]] = value }
    }
}
