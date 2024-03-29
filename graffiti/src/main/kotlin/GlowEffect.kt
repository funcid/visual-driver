import dev.xdark.clientapi.event.window.WindowResize
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.opengl.GL11
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.rectangle

object GlowEffect {

    private val vignette = rectangle {
        size = UIEngine.overlayContext.size

        color = Color(0, 0, 0, 0.0)

        textureLocation = ResourceLocation.of("minecraft", "textures/misc/vignette.png")
        beforeRender {
            GlStateManager.disableDepth()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        }
        afterRender {
            GlStateManager.enableDepth()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        }
    }

    init {
        mod.registerHandler<WindowResize> { vignette.size = UIEngine.overlayContext.size }
    }

    fun showAlways(red: Int, green: Int, blue: Int, power: Double) {
        mod.gui + vignette

        vignette.color.red = red
        vignette.color.blue = blue
        vignette.color.green = green
        vignette.color.alpha = power
    }
}