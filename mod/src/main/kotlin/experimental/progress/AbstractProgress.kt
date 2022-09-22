package experimental.progress

import experimental.progress.ProgressController.Companion.PROGRESS_WIDTH
import lazyCarved
import lazyText
import me.func.protocol.ui.progress.Progress
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*

abstract class AbstractProgress(val model: Progress) {

    var enabled: Boolean = false

    val content: TextElement by lazyText {
        origin = TOP
        align = TOP
        color = WHITE
        content = "Загрузка..."
        offset.y -= 12
    }

    val progress: CarvedRectangle by lazyCarved {
        origin = LEFT
        align = LEFT
        size = V3(PROGRESS_WIDTH * model.progress, 5.0, 0.0)
        color = Color(42, 102, 189, 1.0)
    }

    val container: CarvedRectangle by lazyCarved {
        origin = TOP
        align = TOP
        size = V3(PROGRESS_WIDTH, 5.0, 0.0)
        color = Color(0, 0, 0, 0.62)

        +progress
        +content
    }

    abstract fun create()

    abstract fun remove()
}