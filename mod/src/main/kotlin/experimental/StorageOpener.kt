package experimental

import dev.xdark.feder.NetUtil
import me.func.protocol.gui.Storage
import me.func.protocol.gui.StoragePosition
import sun.security.jgss.GSSToken.readInt
import java.util.*

context(KotlinMod)
class StorageOpener {

    init {
        registerChannel("storage:open") {
            Storage(
                UUID.fromString(NetUtil.readUtf8(this)),
                NetUtil.readUtf8(this), // title
                NetUtil.readUtf8(this), // money title
                NetUtil.readUtf8(this), // hint
                readInt(), // rows
                readInt(), // columns
                MutableList(readInt()) { // item count
                    StoragePosition(
                        NetUtil.readUtf8(this), // texture
                        readInt(), // prize
                        NetUtil.readUtf8(this), // item title
                        NetUtil.readUtf8(this), // item description
                    )
                }
            ).open()
        }
    }

}