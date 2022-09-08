package experimental.storage.menu.selection

import Main.Companion.menuStack
import dev.xdark.feder.NetUtil
import experimental.storage.menu.MenuManager
import me.func.protocol.menu.SelectionModel
import readJson
import ru.cristalix.clientapi.KotlinModHolder.mod
import java.util.*

class SelectionManager {

    companion object {
        fun run() {
            println("Selection manager loaded!")

            fun push(selection: Selection) {
                MenuManager.push(selection)
            }

            mod.registerChannel("storage:open-json") {
                println("Open new selection menu from json.")
                val model = readJson<SelectionModel>()
                push(Selection(model))
            }

            mod.registerChannel("storage:open") {
                println("Open new selection menu from data.")
                push(
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

                println("Loaded page response from $uuid")

                if (menuStack.size < 1) return@registerChannel
                val menu = menuStack.peek()
                if (menu !is Selection || uuid != menu.uuid.toString()) return@registerChannel
                val page = readInt()
                if (page < 0 || page > menu.pageCount) return@registerChannel
                val currentPage = menu.pages[page]

                println("Read icons on page $page")

                val data = MenuManager.readIcons(this)
                // Добавляем хинт кнопкам
                data.forEach { it.hint = if (it.hint == null || it.hint?.isEmpty() == true) menu.hint else it.hint }
                currentPage.content = data
                menu.storage.addAll(data)
                menu.redrawGrid()
            }
        }
    }
}