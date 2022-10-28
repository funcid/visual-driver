package me.func.mod.ui.menu

import me.func.mod.conversation.ModTransfer
import me.func.mod.reactive.ReactiveButton
import me.func.protocol.ui.menu.Page
import org.bukkit.entity.Player
import kotlin.math.ceil

interface Paginated : Storage, Page {

    // Вместимость страницы
    fun getPageCapacity() = rows * columns

    // Получить кнопки на странице
    fun getContentByPage(page: Int): List<ReactiveButton> {
        val capacity = getPageCapacity()
        return storage.drop(capacity * maxOf(0, page)).take(capacity)
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
            .string(uuid.toString()) // uuid меню
            .integer(page) // данная страница
            .integer(content.size) // количество кнопок
            .apply { content.forEach { it.write(this) } } // контент
            .send("func:page-response", player)
    }
}