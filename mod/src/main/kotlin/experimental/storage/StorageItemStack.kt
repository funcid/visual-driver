package experimental.storage

import dev.xdark.clientapi.item.ItemStack
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.item

class StorageItemStack(icon: ItemStack, price: Long, title: String, description: String): StorageNode(price, title, description) {

    val icon = item { stack = icon }

    override fun withPadding(padding: Double) = icon.apply {
        scale = V3(padding / 16.0, padding / 16.0, padding / 16.0)
    }

    override fun render() = icon.render()
}