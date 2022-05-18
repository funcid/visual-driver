package experimental.storage

import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle

class StorageItemTexture(icon: String, price: Long, title: String, description: String) :
    StorageNode<RectangleElement>(price, title, description, rectangle {
        val parts = icon.split(":", limit = 2)
        textureLocation = ResourceLocation.of(parts.first(), parts.last())
    }) {
    override fun scaling(scale: Double) = icon.apply {
        size = V3(scale, scale, scale)
    }
}