package standard.storage.button

import dev.xdark.clientapi.item.ItemStack
import ru.cristalix.uiengine.element.ItemElement
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.item

class StorageItemStack(icon: ItemStack) : StorageNode<ItemElement>(item { stack = icon }) {
    override fun scaling(scale: Double) = icon.apply {
        this.scale = V3(scale / 16.0, scale / 16.0, scale / 16.0)
    }
}
