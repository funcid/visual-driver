package experimental.storage.button

import Main.Companion.externalManager
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle

class StorageItemTexture(icon: String) :
    StorageNode<RectangleElement>(rectangle { textureLocation = externalManager.load(icon) }) {
    override fun scaling(scale: Double) = icon.apply {
        size = V3(scale, scale, scale)
    }
}
