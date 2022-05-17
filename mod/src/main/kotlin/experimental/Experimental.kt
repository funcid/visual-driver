package experimental

import dev.xdark.clientapi.item.ItemTools
import dev.xdark.feder.NetUtil
import experimental.storage.*
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine.clientApi
import selectionStack
import sun.security.jgss.GSSToken.readInt
import java.util.*

context(KotlinMod)
class Experimental {

    init {
        Banners()
        GlowPlaces()
        Recharge()
        Disguise()
        Reconnect()

        registerChannel("func:accept") {
            Confirmation(UUID.fromString(NetUtil.readUtf8(this)), NetUtil.readUtf8(this)).open()
            selectionStack.clear()
        }

        registerChannel("storage:open") {
            val screen = StorageMenu(
                UUID.fromString(NetUtil.readUtf8(this)),
                NetUtil.readUtf8(this), // title
                NetUtil.readUtf8(this), // vault
                NetUtil.readUtf8(this), // money title
                NetUtil.readUtf8(this), // hint
                readInt(), // rows
                readInt(), // columns
                MutableList(readInt()) { // item count
                    if (readBoolean()) { // real item
                        StorageItemStack(
                            ItemTools.read(this), // item
                            readLong(), // prize
                            NetUtil.readUtf8(this), // item title
                            NetUtil.readUtf8(this), // item description
                        )
                    } else { // texture
                        StorageItemTexture(
                            NetUtil.readUtf8(this), // texture
                            readLong(), // prize
                            NetUtil.readUtf8(this), // item title
                            NetUtil.readUtf8(this), // item description
                        )
                    }
                })
            screen.open()
            selectionStack.push(screen)
        }
    }
}
