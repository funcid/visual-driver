package me.func.mod.menu

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player
import kotlin.math.ceil

interface Paginated : Storage {

    var rows: Int
    var columns: Int

    // Вместимость страницы
    fun getPageCapacity() = rows * columns

    // Получить кнопки на странице
    fun getContentByPage(page: Int): List<Button> {
        val capacity = getPageCapacity()
        return storage.drop(capacity * (page - 1)).take(capacity)
    }

    // Количество страниц
    fun getPageCount() = ceil(storage.size * 1.0 / getPageCapacity()).toInt()

    // Проверить, существует ли данная страница
    fun isPageExists(page: Int) = page in 0..getPageCount()

    // Отправить страницу
    fun sendPage(page: Int, player: Player) {
        if (!isPageExists(page)) return
        val content = getContentByPage(page)

        ModTransfer()
            .string(uuid.toString())
            .integer(content.size)
            .apply { content.forEach { it.write(this) } }
            .send("func:page-response", player)
    }

}