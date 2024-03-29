package me.func.protocol.ui.progress

import me.func.protocol.Unique
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.color.RGB
import me.func.protocol.math.Position
import java.util.*

open class Progress(
    override var uuid: UUID = UUID.randomUUID(),
    open var position: Position = Position.TOP,
    open var lineColor: RGB = GlowColor.BLUE,
    open var text: String = "Загрузка...",
    open var offsetY: Double = 0.0,
    open var offsetX: Double = 0.0,
    open var offsetZ: Double = 0.0,
    open var scale: Double = 1.0,
    open var hideOnTab: Boolean = true,
): Unique {

    open var progress: Double = 0.5 // Прогресс между 0.0 и 1.0

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(val model: Progress = Progress()) {

        fun uuid(uuid: UUID) = apply { model.uuid = uuid }
        fun position(position: Position) = apply { model.position = position }
        fun color(lineColor: RGB) = apply { model.lineColor = lineColor }
        fun text(text: String) = apply { model.text = text }
        fun offsetY(offsetY: Double) = apply { model.offsetY = offsetY }
        fun offsetX(offsetX: Double) = apply { model.offsetX = offsetX }
        fun offsetZ(offsetZ: Double) = apply { model.offsetZ = offsetZ }
        fun progress(progress: Double) = apply { model.progress = progress }
        fun scale(scale: Double) = apply { model.scale = scale }
        fun hideOnTab(hideOnTab: Boolean) = apply { model.hideOnTab = hideOnTab }
        fun build() = model
    }

}
