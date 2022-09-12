package experimental.progress

import experimental.progress.ProgressController.Companion.PROGRESS_WIDTH
import lazyCarved
import lazyText
import me.func.protocol.progress.Progress
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*
import java.util.*

class UIProgress(
    override var uuid: UUID = UUID.randomUUID(),
    override val model: Progress = ProgressController.EMPTY_PROGRESS,
    override var enabled: Boolean = false
) : AbstractProgress {

    override val content: TextElement by lazyText {
        origin = TOP
        align = TOP
        color = WHITE
        shadow = true
        content = "Загрузка..."
        offset.y -= 15
    }

    override val progress: CarvedRectangle by lazyCarved {
        origin = LEFT
        align = LEFT
        size = V3(PROGRESS_WIDTH, 5.0, 0.0)
        color = Color(42, 102, 189, 1.0)
    }

    override val container: CarvedRectangle by lazyCarved {
        offset.y += 30
        origin = TOP
        align = TOP
        size = V3(PROGRESS_WIDTH, 5.0, 0.0)
        color = Color(0, 0, 0, 0.62)
        +progress
        +content
        UIEngine.overlayContext + this
    }
}