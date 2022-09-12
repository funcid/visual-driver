package experimental.progress

import dev.xdark.clientapi.event.render.RenderTickPre
import me.func.protocol.math.Position
import me.func.protocol.progress.Progress
import org.lwjgl.input.Keyboard
import readColoredUtf8
import readRgb
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.util.*

class ProgressController {

    companion object {
        val EMPTY_PROGRESS = Progress.builder().build()
        const val PROGRESS_WIDTH = 180.0
    }

    private val progressMap = hashMapOf<UUID, AbstractProgress>() // uuid to progress

    init {
        mod.registerHandler<RenderTickPre> {
            progressMap.values.forEach { progress ->
                val container = progress.container
                val data = progress.model

                // Скрывать при нажатии TAB
                if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && data.hideOnTab)
                    container.enabled = false
                else if (!container.enabled && progress.enabled)
                    container.enabled = true

                // Смещение
                container.offset.y = -data.offsetY
                container.offset.x = data.offsetX
            }
        }

        mod.registerChannel("progress-ui:update") {

            progressMap[readId()]?.let { progress ->
                progress.content.content = readColoredUtf8()
                progress.model.progress = readDouble()
                progress.progress.animate(0.1) { size.x = PROGRESS_WIDTH * progress.model.progress }
            }
        }

        mod.registerChannel("progress-ui:remove") {

            val progress = progressMap[readId()] ?: return@registerChannel
            UIEngine.overlayContext.removeChild(progress.container)
            progressMap.remove(progress.uuid)
        }

        mod.registerChannel("progress-ui:create") {

            val uuid = readId()
            val progress = UIProgress(
                uuid, Progress.builder()
                    .color(readRgb())
                    .position(Position.values()[readInt()])
                    .hideOnTab(readBoolean())
                    .offsetX(readDouble())
                    .offsetY(readDouble())
                    .progress(readDouble())
                    .text(readColoredUtf8())
                    .build()
            )

            progress.enabled = true
            progress.content.content = progress.model.text // текст
            progress.container.color = Color(
                progress.model.lineColor.red,
                progress.model.lineColor.green,
                progress.model.lineColor.blue
            ) // цвет
            val origin = when (progress.model.position) {
                Position.RIGHT -> RIGHT
                Position.LEFT -> LEFT
                Position.TOP -> TOP
                Position.BOTTOM -> BOTTOM
                Position.BOTTOM_LEFT -> BOTTOM_LEFT
                Position.BOTTOM_RIGHT -> BOTTOM_RIGHT
                Position.TOP_LEFT -> TOP_LEFT
                Position.TOP_RIGHT -> TOP_RIGHT
            } // положение

            progress.container.align = origin
            progress.container.origin = origin

            progressMap[uuid] = progress
        }
    }

}