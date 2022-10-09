package experimental.panel

import dev.xdark.feder.NetUtil
import me.func.protocol.data.color.RGB
import readColoredUtf8
import readRgb
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.awt.Color.*
import java.util.UUID

class Panel {

    private val stats = hashMapOf<UUID, PanelBlock>()

    val intervalSize =  2.0
    val char = 6.0

    private val container = flex {
        flexSpacing = intervalSize
        flexDirection = FlexDirection.RIGHT
        align = BOTTOM
        origin = CENTER
        offset.y -= 30.0
    }

    init {
        UIEngine.overlayContext + container

        mod.registerChannel("func:panel-new") {
            val uuid = readId()
            val text = readColoredUtf8()
            val progress = readDouble()
            val rgb = readRgb()

            val element = emptyCarvedElement()
            val textElement = emptyTextElement()
            val progressElement = emptyCarvedElement().apply {
                align = CENTER
                origin = CENTER
            }
            val data = PanelBlock(uuid, element, textElement, progressElement, progress)

            removeElement(uuid)

            stats[uuid] = data
            changeText(uuid, text)
            changeProgress(uuid, progress)
            changeColor(uuid, rgb)

            element + progressElement
            element + textElement
            container + element
        }

        mod.registerChannel("func:panel-remove") {
            val uuid = readId()

            removeElement(uuid)
        }

        mod.registerChannel("func:panel-update") {
            val uuid = readId()

            when (readInt()) {
                0 -> changeProgress(uuid, readDouble())
                1 -> changeText(uuid, readColoredUtf8())
                2 -> changeColor(uuid, readRgb())
            }
        }
    }

    fun changeText(uuid: UUID, text: String) {
        val data = getPanel(uuid) ?: return

        data.text.content = text
        resize()
    }

    fun changeColor(uuid: UUID, rgb: RGB) {
        val data = getPanel(uuid) ?: return
        data.progress.color = Color(rgb.red, rgb.green, rgb.blue)
    }

    fun changeProgress(uuid: UUID, progress: Double) {
        val data = getPanel(uuid) ?: return

        if (progress == 0.0) {
            data.progress.enabled = false
            return
        } else if (!data.progress.enabled && progress > 0.0) {
            data.progress.enabled = true
        }

        data.currentProgress = progress
        resize()
    }

    fun removeElement(uuid: UUID) {
        val data = getPanel(uuid) ?: return

        stats.remove(uuid)
        container.removeChild(data.element)
        data.element.enabled = false
    }

    fun resize() {
        val chars = stats.values.sumOf { it.text.content.length }

        stats.forEach { (_, stat) ->
            val length = stat.text.content.length
            val size = (182.0 - intervalSize * (stats.size - 1)) * length / chars

            stat.element.size.x = size

            if (stat.text.content.length * char > size) {
                val amountChars = (size / char).toInt() - 3
                stat.text.apply {
                    content = content.take(amountChars) + "..."
                }
            }

            stat.progress.animate(0.25, Easings.QUART_OUT) {
                this.size.x = size * stat.currentProgress
            }
        }
    }

    fun getPanel(uuid: UUID) = stats[uuid]

    fun emptyTextElement() = text {
        content = " "
        shadow = true
        align = CENTER
        origin = CENTER
    }

    fun emptyCarvedElement() = carved {
        size = V3(0.0, 13.0)
        color = Color(0, 0, 0, 0.62)
    }
}