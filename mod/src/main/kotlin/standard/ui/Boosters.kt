package standard.ui

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.gui.ingame.ChatScreen
import io.netty.buffer.Unpooled
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.math.pow
import kotlin.properties.Delegates.notNull

class Boosters {

    private var boostersContainer: Flex by notNull()

    init {
        val container = flex {
            origin = TOP_RIGHT
            align = TOP_RIGHT
            flexSpacing = 4.0
            size = V3(80.0, 80.0)
            offset.y = 12.0
            offset.x -= 12.0

            boostersContainer = +flex {
                flexSpacing = 4.0
            }
            enabled = true
        }

        mod.registerHandler<GameLoop> {
            val isChatDisabled = UIEngine.clientApi.minecraft().currentScreen() !is ChatScreen
            container.enabled = !Keyboard.isKeyDown(Keyboard.KEY_TAB) && isChatDisabled
        }

        mod.registerChannel("zabelov:boosters") {
            boostersContainer.children.clear()
            println("Clear boosters")

            repeat(readInt()) {
                val name = readUtf8()
                val multiplier = readDouble()
                boostersContainer + booster(name, multiplier)
            }
            println("Boosters: ${boostersContainer.children.size}")
        }

        UIEngine.overlayContext.addChild(container)
    }

    private fun booster(name: String, multiplier: Double) = carved {
        val length = name.length
        size = V3(62.5 * 1.055.pow(length.toDouble()), 18.0)
        color.alpha = 0.62
        carveSize = 2.0

        +text {
            content = name
            size = V3(16.0, 16.0)
            color = WHITE
            shadow = true
            origin = CENTER
            align = CENTER
            offset.y = 3.5
            offset.x -= 12.0 - (-0.3 * length)
        }

        +carved {
            size = V3(32.0, 18.0)
            color = Color(40, 180, 0, .62)
            carveSize = 2.0
            origin = RIGHT
            align = RIGHT

            +text {
                content = "x$multiplier".replace(".0", "")
                size = V3(16.0, 16.0)
                color = WHITE
                origin = CENTER
                align = CENTER
                shadow = true
                offset.y = 3.5
            }
        }
    }
}