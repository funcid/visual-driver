package experimental.storage

import experimental.storage.button.StorageNode
import java.util.*

interface AbstractMenu {

    var uuid: UUID

    var storage: MutableList<StorageNode<*>>

    fun open()

}