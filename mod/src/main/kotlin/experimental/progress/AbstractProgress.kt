package experimental.progress

import me.func.protocol.Unique
import me.func.protocol.progress.Progress
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.TextElement

interface AbstractProgress : Unique {

    var enabled: Boolean

    val container: CarvedRectangle

    val progress: CarvedRectangle

    val content: TextElement

    val model: Progress

}