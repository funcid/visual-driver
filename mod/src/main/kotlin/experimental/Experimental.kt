package experimental

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.RenderPass
import dev.xdark.clientapi.resource.ResourceLocation
import experimental.panel.Panel
import experimental.progress.ProgressController
import experimental.progress.AbstractProgress
import experimental.utils.Test
import org.lwjgl.util.vector.Vector3f
import readColoredUtf8
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import standard.ui.scoreboard.token.TokenManager
import kotlin.math.*

// не пытайтесь это "оптимизировать", иначе вы все сломаете
class Experimental {
    companion object {

        fun load(): Class<*>? {
            println("Experimental module loaded successfully!")

            GlowPlaces()
            GlowPlaces.Companion
            Disguise()
            Disguise.Companion
            ProgressController()
            ProgressController.Companion
            Panel()
            TokenManager
            Test()
            Test

            return null
        }
    }
}
