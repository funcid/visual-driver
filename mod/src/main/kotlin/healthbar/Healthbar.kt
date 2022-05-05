package healthbar

import Mod
import dev.xdark.clientapi.entity.EntityLivingBase
import dev.xdark.clientapi.event.render.NameTemplateRender
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.opengl.GlStateManager
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.Relative
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.rotationMatrix

/**
 * @project animation-api
 * @author Рейдж
 */
context(KotlinMod)
class Healthbar : Mod {
    override fun load() {
        val context = Context3D(V3())
        val bar = rectangle {
            origin = Relative.LEFT
            align = Relative.LEFT
            offset.x = 0.5
            size.x = 10.0
            size.y = 3.0
            color = Color(51, 240, 51)
        }

        val body = rectangle {
            size = V3(16.0, 4.0)
            color = Color(0, 0, 0, 0.5)
            origin = Relative.CENTER
            addChild(bar)
        }

        context.addChild(body)

        registerHandler<RenderTickPre> {
            val player = UIEngine.clientApi.minecraft().player
            val matrix = Matrix4f()
            Matrix4f.setIdentity(matrix)
            Matrix4f.rotate(
                ((player.rotationYaw + 180) / 180 * Math.PI).toFloat(),
                Vector3f(0f, -1f, 0f),
                matrix,
                matrix
            )
            Matrix4f.rotate((player.rotationPitch / 180 * Math.PI).toFloat(), Vector3f(-1f, 0f, 0f), matrix, matrix)
            context.matrices[rotationMatrix] = matrix
        }

        registerHandler<NameTemplateRender> a@{
            if (entity !is EntityLivingBase) return@a
            val entity = entity as EntityLivingBase
            val part = entity.health / entity.maxHealth
            if (part == 1f) return@a

            val partialTicks = UIEngine.clientApi.minecraft().timer.renderPartialTicks
            context.offset = V3(
                entity.lastX + (entity.x - entity.lastX) * partialTicks,
                entity.lastY + (entity.y - entity.lastY) * partialTicks + entity.eyeHeight + 1,
                entity.lastZ + (entity.z - entity.lastZ) * partialTicks
            )

            val width = (entity.maxHealth * 2).coerceAtMost(30f).toDouble()
            bar.size.x = width * part
            body.size.x = width + 1.0

            var green = part * 2
            if (green > 1) green = 1f
            var red = (1 - part) * 2
            if (red > 1) red = 1f
            bar.color.green = (green * 255).toInt()
            bar.color.red = (red * 255).toInt()

            GlStateManager.disableLighting()
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glDepthMask(false)

            context.transformAndRender()
            GlStateManager.enableLighting()
            GL11.glDepthMask(true)
        }
    }
}