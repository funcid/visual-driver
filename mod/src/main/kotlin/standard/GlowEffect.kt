package standard

import dev.xdark.clientapi.event.window.WindowResize
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.opengl.GL11
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.rectangle

import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.registerHandler
import sun.security.jgss.GSSToken.readInt

context(KotlinMod)
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
        registerChannel("func:glow-short") {
            show(readDouble(), readInt(), readInt(), readInt(), readDouble())
        }

        registerChannel("func:glow") {
            showAlways(readInt(), readInt(), readInt(), readDouble())
        }
    }

    private fun showAlways(red: Int, blue: Int, green: Int, power: Double) {
        lazy()

        vignette.size = UIEngine.overlayContext.size
        vignette.color.red = red
        vignette.color.blue = blue
        vignette.color.green = green
        vignette.color.alpha = power
    }

    private fun show(duration: Double, red: Int, blue: Int, green: Int, power: Double) {
        lazy()

        vignette.color.red = red
        vignette.color.blue = blue
        vignette.color.green = green
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
