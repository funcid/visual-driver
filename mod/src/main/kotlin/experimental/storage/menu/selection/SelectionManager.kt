package experimental.storage.menu.selection

import Main.Companion.menuStack
import dev.xdark.feder.NetUtil
import experimental.Experimental.Companion.menuManager
import ru.cristalix.clientapi.KotlinModHolder.mod
import java.util.*

class SelectionManager {

    init {
        mod.registerChannel("storage:open") {
           menuManager.push(
                Selection(
                    UUID.fromString(NetUtil.readUtf8(this)),
                    NetUtil.readUtf8(this).replace("&", "§"), // title
                    NetUtil.readUtf8(this), // vault
                    NetUtil.readUtf8(this).replace("&", "§"), // money title
                    NetUtil.readUtf8(this).replace("&", "§"), // hint
                    readInt(), // rows
                    readInt(), // columns
                    readInt(), // page count
                )
            )
        }
        mod.registerChannel("func:page-response") {
            val uuid = NetUtil.readUtf8(this)
            if (menuStack.size < 1) return@registerChannel
            val menu = menuStack.pop()
            if (menu !is Selection || uuid != menu.uuid.toString()) return@registerChannel
            val page = readInt()
            if (page < 0 || page >= menu.pageCount) return@registerChannel
            val currentPage = menu.pages[page]
            currentPage.content = menuManager.readIcons(this)
            menu.redrawGrid()
        }
    }

}