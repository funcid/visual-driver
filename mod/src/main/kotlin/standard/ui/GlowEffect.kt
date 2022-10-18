package standard.ui

import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.opengl.GL11
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.KotlinModHolder
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.rectangle

class GlowEffect {

    private var added = false
    private val vignette = rectangle {
        size = UIEngine.overlayContext.size

        color = Color(0, 0, 0, 0.0)

        textureLocation = ResourceLocation.of("minecraft", "textures/misc/vignette.png")
        beforeRender = {
            GlStateManager.disableDepth()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        }
        afterRender = {
            GlStateManager.enableDepth()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        }
    }

    init {
        mod.registerChannel("func:glow-short") {
            show(readDouble(), readInt(), readInt(), readInt(), readDouble())
        }

        mod.registerChannel("func:glow") {
            showAlways(readInt(), readInt(), readInt(), readDouble())
        }
    }

    private fun showAlways(red: Int, green: Int, blue: Int, power: Double) {
        lazy()

        vignette.size = UIEngine.overlayContext.size
        vignette.color.red = red
        vignette.color.green = green
        vignette.color.blue = blue
        vignette.color.alpha = power
    }

    private fun show(duration: Double, red: Int, green: Int, blue: Int, power: Double) {
        lazy()

        vignette.color.red = red
        vignette.color.green = green
        vignette.color.blue = blue
        vignette.size = UIEngine.overlayContext.size

        vignette.animate(duration) {
            vignette.color.alpha = power
        }
        UIEngine.schedule(duration) {
            vignette.animate(duration) {
                vignette.color.alpha = 0.0
            }
        }
    }

    private fun lazy() {
        if (!added) {
            UIEngine.overlayContext + vignette
            added = true
        }
    }
}
