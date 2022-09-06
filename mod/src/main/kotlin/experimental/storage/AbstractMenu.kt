package experimental.storage

import experimental.storage.button.StorageNode
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.utility.Color
import java.util.UUID

abstract class AbstractMenu(
    open var uuid: UUID = UUID.randomUUID(),
    open var title: String,
    open var storage: MutableList<StorageNode<*>>
) : ContextGui() {
    init {
        color = Color(0, 0, 0, .86)
    }
}