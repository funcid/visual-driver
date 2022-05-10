package experimental.storage

import ru.cristalix.uiengine.element.AbstractElement

abstract class StorageNode(
    var price: Long,
    var title: String,
    var description: String
) : AbstractElement() {

    abstract fun withPadding(padding: Double): AbstractElement

}