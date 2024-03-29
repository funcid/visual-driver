package standard.storage.menu.selection

import Main.Companion.menuStack
import dev.xdark.feder.NetUtil
import me.func.protocol.ui.menu.SelectionModel
import readColoredUtf8
import readJson
import readUuid
import ru.cristalix.clientapi.KotlinModHolder.mod
import standard.storage.button.ButtonReader
import standard.storage.button.StorageItemTexture
import standard.storage.menu.MenuManager
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
                    StorageItemTexture(it.texture ?: "").apply {
                        price = it.price
                        priceText = it.priceText
                        command = it.command
                        title = it.title
                        description = it.description
                        hint = it.hint ?: ""
                        hover = it.hover
                        vault = it.vault
                        backgroundColor = it.backgroundColor
                        enabled = it.enabled
                    }
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
                        readColoredUtf8(), // info
                        readColoredUtf8(), // title
                        NetUtil.readUtf8(this), // vault
                        readColoredUtf8(), // money title
                        readColoredUtf8(), // hint
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

                val data = ButtonReader.readIcons(this)
                // Добавляем хинт кнопкам
                data.forEach { it.hint = if (it.hint == null || it.hint?.isEmpty() == true) menu.hint else it.hint }
                currentPage.content = data
                menu.storage.addAll(data)
                menu.redrawGrid()
            }

            mod.registerChannel("selection:update") {
                if (menuStack.isEmpty()) return@registerChannel
                val menu = menuStack.peek() as? Selection ?: return@registerChannel
                val uuid = readUuid()
                if (uuid != menu.uuid) return@registerChannel

                when (readByte().toInt()) {
                    0 -> {
                        val money = readColoredUtf8()
                        menu.money = money
                        menu.balanceText?.updateTitle(money)
                    }

                    else -> return@registerChannel
                }
            }
        }
    }
}