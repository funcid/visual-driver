package standard.ui

import asColor
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.gui.ingame.ChatScreen
import dev.xdark.clientapi.resource.ResourceLocation
import io.netty.buffer.Unpooled
import lootbox.RED
import me.func.protocol.data.color.GlowColor
import org.lwjgl.input.Keyboard
import org.lwjgl.util.vector.Vector3f
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.onMouseUp
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.math.*
import kotlin.properties.Delegates.notNull

class Boosters {

    private var boostersContainer = flex {
        origin = TOP_LEFT
        align = TOP_LEFT
        flexSpacing = 4.0
        size = V3(80.0, 80.0)
        offset.y = 12.0
        offset.x += 12.0

        flexSpacing = 4.0
        flexDirection = FlexDirection.DOWN
        enabled = true
    }

    private val close = carved {
        size = V3(16.0, 16.0)
        color = GlowColor.RED.asColor()
        carveSize = 2.0

        +text {
            align = CENTER
            origin = CENTER
            content = "X"
            color = WHITE
        }

        onMouseUp {
            boostersContainer.children.clear()
            boostersContainer.addChild(this@carved)
        }
    }

    init {
        boostersContainer + close
        UIEngine.overlayContext.addChild(boostersContainer)

        mod.registerHandler<GameLoop> {
            boostersContainer.enabled = boostersContainer.children.size > 1
        }

        mod.registerChannel("zabelov:boosters") {
            boostersContainer.children.clear()
            boostersContainer + close

            repeat(readInt()) {
                val name = readUtf8()
                val multiplier = "%.2f".format(readDouble())
                boostersContainer + booster(name, multiplier)
                boostersContainer.children.sortByDescending { it.size.x }
            }
        }
    }

    private fun booster(name: String, multiplier: String) = carved {

        val blockSize = 32.0
        val textOffset = 4.0

        val length = UIEngine.clientApi.fontRenderer().getStringWidth(name) + blockSize + 2 * textOffset
        size = V3(length, 18.0)
        color.alpha = 0.62
        carveSize = 2.0

        +text {
            content = name
            color = WHITE
            shadow = true
            origin = LEFT
            align = LEFT
            offset.x += textOffset
        }

        +carved {
            size = V3(blockSize, 18.0)
            color = Color(40, 180, 0, .62)
            carveSize = 2.0
            origin = RIGHT
            align = RIGHT

            +text {
                content = "x$multiplier".replace(".00", "")
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