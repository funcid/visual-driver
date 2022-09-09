package experimental.storage.menu.selection

import Main.Companion.menuStack
import dev.xdark.feder.NetUtil
import experimental.storage.button.StorageItemTexture
import experimental.storage.menu.MenuManager
import me.func.protocol.menu.SelectionModel
import readJson
import ru.cristalix.clientapi.KotlinModHolder.mod
import java.util.*
import kotlin.math.ceil

class SelectionManager {

    companion object {
        fun run() {
            println("Selection manager loaded!")

            mod.registerChannel("storage:open-json") {
                val model = readJson<SelectionModel>()
                val pageSize = model.columns * model.rows

                if (pageSize < 1) {
                    println("Menu page size below one!")
                    return@registerChannel
                }

                val localStorage = model.data.map {
                    StorageItemTexture(
                        it.texture ?: "",
                        it.price,
                        it.title,
                        it.description,
                        it.hint ?: "",
                        it.hover ?: "Купить",
                        it.special
                    ).apply { command = it.command }
                }

                val pageCount = ceil(localStorage.size * 1.0 / pageSize).toInt()

                val pages = MutableList(pageCount) {
                    Page(it, localStorage.drop(it * pageSize).take(pageSize))
                }

                val selection = Selection(model, pages).apply {
                    storage.addAll(localStorage)
                }

                MenuManager.push(selection)
                selection.open()
            }

            mod.registerChannel("storage:open") {
                println("Open new selection menu from data.")
                MenuManager.push(
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