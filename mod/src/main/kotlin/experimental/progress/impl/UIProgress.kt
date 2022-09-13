package experimental.progress.impl

import experimental.progress.AbstractProgress
import me.func.protocol.ui.progress.Progress
import ru.cristalix.uiengine.UIEngine

class UIProgress(model: Progress) : AbstractProgress(model) {

    override fun create() { UIEngine.overlayContext + container }

    override fun remove() { UIEngine.overlayContext.removeChild(container) }
}