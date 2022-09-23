package standard.ui.scoreboard

import dev.xdark.clientapi.opengl.GlStateManager
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.util.*

class ScoreBoard(
    val uuid: UUID,
    header: String,
    footer: String
) : CarvedRectangle() {

    private val headerInfo = +ScoreBoardContent(header)
    private val footerInfo = +ScoreBoardContent(footer)

    var lineKey = "§bЗагрузка"
    var lineValue = "..."

    var lastMaxX = 10.0

    private val dataLine = mutableListOf<RectangleElement>()

    init {
        carveSize = 2.0
        size = V3(100.0, 100.0)
        align = RIGHT
        origin = RIGHT
        offset = V3(-6.0, 0.0)
        scale = V3(1.025, 1.025)
        color = Color(0, 0, 0, 0.62)

        headerInfo.offset.y = 6.0
        footerInfo.offset.y = -6.0

        footerInfo.align = BOTTOM
        footerInfo.origin = BOTTOM

        beforeRender = { GlStateManager.disableDepth() }
        afterRender = { GlStateManager.enableDepth() }
    }

    fun show() {
        UIEngine.overlayContext.addChild(this)
    }

    fun hide() {
        UIEngine.overlayContext.removeChild(this)
    }

    fun update() {
        lastMaxX = 0.0

        val splitKey = lineKey.split("\n")
        val splitValue = lineValue.split("\n")

        val sizeValue = splitValue.size

        dataLine.forEach { children.remove(it) }
        dataLine.clear()

        fun width(text: String) = UIEngine.clientApi.fontRenderer().getStringWidth(text) * 1.0

        var offsetY = 25.0
        var sizeMax = maxOf(lastMaxX, maxOf(width(headerInfo.valueText.content) + 5, width(footerInfo.valueText.content) + 5))

        for ((index, text) in splitKey.withIndex()) {
            var value = ""
            if (index <= sizeValue) value = splitValue[index]

            val sizeData = width("$text$value")
            val totalSize = sizeData + 10.0

            if (sizeMax < totalSize) sizeMax = totalSize

            if (text.isEmpty()) {
                offsetY += 11.0
                continue
            } else {
                val line = +ScoreBoardLine(text, value) {
                    size.x = sizeMax
                    offset.y = offsetY
                }

                dataLine.add(line)
            }

            offsetY += 12.0
        }

        lastMaxX = sizeMax

        animate(0.228, Easings.ELASTIC_OUT) {
            dataLine.forEach { it.size.x = sizeMax }

            headerInfo.size.x = sizeMax
            footerInfo.size.x = sizeMax

            size = V3(sizeMax + 12.0, offsetY + 25.0)
        }
    }
}