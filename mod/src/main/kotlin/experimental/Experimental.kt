package experimental

import backMenu
import dev.xdark.clientapi.gui.ingame.AdvancementsScreen
import dev.xdark.clientapi.gui.ingame.InventorySurvivalScreen
import dev.xdark.clientapi.gui.ingame.OptionsScreen
import dev.xdark.clientapi.inventory.Inventory
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.feder.NetUtil
import experimental.storage.*
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine.clientApi
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
            backMenu = null
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
            val mc = clientApi.minecraft()
            backMenu = if (backMenu == null && (mc.inGameHasFocus() || mc.currentScreen() == null || mc.currentScreen() is InventorySurvivalScreen)) screen else backMenu
            screen.open()
        }
    }
}
