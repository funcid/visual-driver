package experimental.storage.button

import Main.Companion.externalManager
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle

class StorageItemTexture(
    icon: String,
    price: Long,
    vault: String,
    title: String,
    description: String,
    hint: String,
    hover: String,
    special: Boolean
) :
    StorageNode<RectangleElement>(
        price,
        vault,
        title,
        description,
        hint,
        hover,
        rectangle { textureLocation = externalManager.load(icon) },
        special
    ) {
    override fun scaling(scale: Double) = icon.apply {
        size = V3(scale, scale, scale)
    }
}
