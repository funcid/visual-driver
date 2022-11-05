package me.func.mod.ui.token

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Function

class Token {

    val uuid = UUID.randomUUID()
    var titleGenerator: Function<Player, String> = Function { "Название" }
    var contentGenerator: Function<Player, String> = Function { "Содержимое" }

    companion object {

        @JvmStatic
        fun builder() = Builder()

        class Builder(private val scheme: Token = Token()) {

            fun title(title: String) = apply { scheme.titleGenerator = Function { title } }
            fun title(generator: Function<Player, String>) = apply { scheme.titleGenerator = generator }
            fun content(description: String) = apply { scheme.contentGenerator = Function { description } }
            fun content(generator: Function<Player, String>) = apply { scheme.contentGenerator = generator }

            fun build() = scheme
        }
    }

    fun update(subscribers: List<Player>) = subscribers.forEach {
        ModTransfer()
            .uuid(uuid)
            .string(titleGenerator.apply(it))
            .string(contentGenerator.apply(it))
            .send("token:update", it)
    }

}