package standard.storage.menu.selection

import dev.xdark.feder.NetUtil
import standard.storage.button.StorageNode
import io.netty.buffer.Unpooled
import ru.cristalix.uiengine.UIEngine
import java.util.*

class Page(
    var index: Int,
    var content: List<StorageNode<*>>? = null
) {
    fun isLoaded() = content != null

    fun load(menu: UUID) {
        UIEngine.clientApi.clientConnection().sendPayload("func:page-request", Unpooled.buffer().apply {
            NetUtil.writeUtf8(this, menu.toString())
            writeInt(index)
        })
    }
}