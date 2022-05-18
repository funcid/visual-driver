package experimental.storage

import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.CarvedRectangle

abstract class StorageNode(
    var price: Long,
    var title: String,
    var description: String
) : AbstractElement() {

    var fullElement: CarvedRectangle? = null

    abstract fun withPadding(padding: Double): AbstractElement

}