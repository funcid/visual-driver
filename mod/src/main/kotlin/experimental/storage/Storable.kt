package experimental.storage

import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.utility.Color
import java.util.*

abstract class Storable(
    open var uuid: UUID = UUID.randomUUID(),
    open var title: String,
    open var storage: MutableList<StorageNode<*>>
) : ContextGui() {
    init {
        color = Color(0, 0, 0, .86)
    }
}