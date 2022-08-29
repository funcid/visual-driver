package experimental.storage

import dev.xdark.clientapi.item.ItemStack
import ru.cristalix.uiengine.element.ItemElement
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.item

class StorageItemStack(
    icon: ItemStack,
    price: Long,
    title: String,
    description: String,
    hover: String,
    oldHover: Boolean,
    special: Boolean
) : StorageNode<ItemElement>(
    price,
    title,
    description,
    hover,
    oldHover,
    item { stack = icon },
    special
) {
    override fun scaling(scale: Double) = icon.apply {
        this.scale = V3(scale / 16.0, scale / 16.0, scale / 16.0)
    }
}
