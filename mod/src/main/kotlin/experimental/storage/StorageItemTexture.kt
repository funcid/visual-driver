package experimental.storage

import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle

class StorageItemTexture(icon: String, price: Long, title: String, description: String): StorageNode(price, title, description) {

    val icon = rectangle {
        val parts = icon.split(":")
        textureLocation = ResourceLocation.of(parts.first(), parts.last())
    }

    override fun withPadding(padding: Double) = icon.apply {
        size = V3(padding, padding, padding)
    }

    override fun render() = icon.render()
}