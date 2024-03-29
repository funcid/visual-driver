package experimental.progress.impl

import experimental.progress.AbstractProgress
import me.func.protocol.ui.progress.Progress
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.utility.V3

class WorldProgress(model: Progress) : AbstractProgress(model) {

    val context: Context3D = Context3D(V3(model.offsetX, model.offsetY, model.offsetZ)).apply {
        addChild(container)
        scale = V3(0.0125 * model.scale, 0.0125 * model.scale, 0.0125 * model.scale)
    }

    override fun create() { UIEngine.worldContexts.add(context) }

    override fun remove() { UIEngine.worldContexts.remove(context) }
}