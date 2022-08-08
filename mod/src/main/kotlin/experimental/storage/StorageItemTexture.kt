package experimental.storage

import Main.Companion.externalManager
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle

class StorageItemTexture(icon: String, price: Long, title: String, description: String, hover: String) :
    StorageNode<RectangleElement>(
        price,
        title,
        description,
        hover,
        rectangle { textureLocation = externalManager.load(icon) },
    ) {
    override fun scaling(scale: Double) = icon.apply {
        size = V3(scale, scale, scale)
    }
}
