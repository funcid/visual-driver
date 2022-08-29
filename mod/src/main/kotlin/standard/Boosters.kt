package standard

import Main.Companion.externalManager
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*
import kotlin.math.pow
import kotlin.properties.Delegates.notNull

class Boosters {

    private var boosters: Flex by notNull()

    private fun booster(name: String, factor: Double, iconResource: String) = carved {
        size = V3(68.0 * 1.04.pow(name.length.toDouble()), 18.0)
        color = Color(0, 0, 0, .62)
        carveSize = 2.0

        +rectangle {
            textureLocation = externalManager.load("runtime:$iconResource")
            color = WHITE
            origin = CENTER
            align = CENTER
            offset.x -= (this@carved.size.x / 2) - 8.0
            size = V3(8.0, 8.0, 8.0)
        }

        +text {
            content = name
            size = V3(16.0, 16.0)
            color = WHITE
            shadow = true
            origin = CENTER
            align = CENTER
            offset.y = 2.5
            offset.x -= 7.0 - (.7 * name.length)
        }

        +carved {
            size = V3(24.0, 18.0)
            color = Color(40, 180, 0, .62)
            carveSize = 2.0
            origin = RIGHT
            align = RIGHT

            +text {
                content = "x$factor"
                size = V3(16.0, 16.0)
                color = WHITE
                origin = CENTER
                align = CENTER
                shadow = true
                offset.y = 2.5
                offset.x = 0.5
            }
        }
    }

    init {
        val container = flex {
            origin = TOP_RIGHT
            align = TOP_RIGHT
            flexSpacing = 5.0
            size = V3(100.0, 100.0)
            offset.y = 2.0
            offset.x -= 2.0

            +carved {
                size = V3(18.0, 18.0)
                color = Color(0, 0, 0, .62)
                carveSize = 2.0

                +text {
                    content = "+"
                    size = V3(16.0, 16.0)
                    color = WHITE
                    shadow = true
                    origin = CENTER
                    align = CENTER
                    offset.y = 3.5
                    offset.x = 0.5
                }
            }

            boosters = +flex {
                flexSpacing = 5.0
            }
            enabled = false
        }

        mod.registerChannel("mid:boostenable") {
            boosters.enabled = readBoolean()
        }

        mod.registerChannel("mid:boost") {
            val count = readInt()

            boosters.children.forEach {
                boosters.removeChild(it)
            }

            repeat(count) {
                boosters.addChild(booster(readUtf8(), readDouble(), readUtf8()))
            }
        }

        UIEngine.overlayContext.addChild(container)
    }
}