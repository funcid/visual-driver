package experimental.progress

import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.event.render.ScaleChange
import dev.xdark.clientapi.event.window.WindowResize
import experimental.progress.impl.UIProgress
import experimental.progress.impl.WorldProgress
import me.func.protocol.math.Position
import me.func.protocol.ui.progress.Progress
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
        const val PROGRESS_WIDTH = 182.0
    }

    private val progressMap = hashMapOf<UUID, AbstractProgress>() // uuid to progress

    init {
        fun rescale() {

            progressMap.values.filterIsInstance<UIProgress>().forEach { progress ->
                // Смещение
                val lines = progress.content.content.lines().size
                val offsetStrings = lines * 1.0 * -(if (lines == 1) 12 else 10)
                progress.container.offset.y = -progress.model.offsetY
                progress.container.offset.x = progress.model.offsetX
                progress.content.offset.y = offsetStrings
            }
        }

        mod.registerHandler<ScaleChange> { rescale() }
        mod.registerHandler<WindowResize> { rescale() }

        mod.registerHandler<RenderTickPre> {

            progressMap.values.forEach { progress ->

                val container = progress.container
                val data = progress.model

                if (progress is UIProgress) {

                    // Скрывать при нажатии TAB
                    if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && data.hideOnTab)
                        container.enabled = false
                    else if (!container.enabled && progress.enabled)
                        container.enabled = true
                } else if (progress is WorldProgress) {

                    val minecraft = UIEngine.clientApi.minecraft()
                    val player = minecraft.player

                    // Поворачивать на игрока
                    val timer = minecraft.timer
                    val yaw =
                        (player.rotationYaw - player.prevRotationYaw) * timer.renderPartialTicks + player.prevRotationYaw
                    val pitch =
                        (player.rotationPitch - player.prevRotationPitch) * timer.renderPartialTicks + player.prevRotationPitch

                    progress.context.rotation = Rotation(-yaw * Math.PI / 180 + Math.PI, 0.0, 1.0, 0.0)
                    progress.container.rotation = Rotation(-pitch * Math.PI / 180, 1.0, 0.0, 0.0)
                }
            }
        }

        mod.registerChannel("progress-ui:update") {

            progressMap[readId()]?.let { bar ->
                when (readInt()) { // код изменения
                    0 -> { // прогресс
                        val progress = readDouble()
                        if (progress !in 0.0..1.0)
                            return@registerChannel
                        bar.model.progress = progress

                        bar.progress.animate(0.25) {
                            size.x = PROGRESS_WIDTH * progress
                            enabled = progress > 0.0
                        }
                    }
                    1 -> bar.content.content = readColoredUtf8() // текст
                }
            }
        }

        mod.registerChannel("progress-ui:remove") {

            val progress = progressMap[readId()] ?: return@registerChannel
            progress.remove()
            progressMap.remove(progress.model.uuid)
        }

        mod.registerChannel("progress-ui:create") {

            val uuid = readId()

            val model = Progress.builder()
                .color(readRgb())
                .position(Position.values()[readInt()])
                .hideOnTab(readBoolean())
                .offsetX(readDouble())
                .offsetY(readDouble())
                .offsetZ(readDouble())
                .scale(readDouble())
                .progress(readDouble())
                .text(readColoredUtf8())
                .build()

            val progress: AbstractProgress = if (model.offsetZ == 0.0) UIProgress(model) else WorldProgress(model)

            progressMap[uuid] = progress

            progress.enabled = true
            progress.content.content = progress.model.text // текст
            progress.progress.color = Color(
                progress.model.lineColor.red,
                progress.model.lineColor.green,
                progress.model.lineColor.blue
            ) // цвет

            if (progress is UIProgress) {
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

                rescale()
            } else {
                progress.progress.offset.z -= 0.001
            }

            progress.create()
        }
    }

}