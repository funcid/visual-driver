package experimental

import dev.xdark.clientapi.item.ItemTools
import dev.xdark.feder.NetUtil
import experimental.storage.StorageItemStack
import experimental.storage.StorageItemTexture
import experimental.storage.StorageMenu
import sun.security.jgss.GSSToken.readInt
import ru.cristalix.clientapi.KotlinMod
import java.util.*

context(KotlinMod)
class Experimental {
    init {
        Banners()
        GlowPlaces()
        Recharge()
        Disguise()

        registerChannel("storage:open") {
            StorageMenu(
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
                }).open()
        }
    }
}
