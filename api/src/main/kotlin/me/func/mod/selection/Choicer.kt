package me.func.mod.selection

import me.func.mod.selection.MenuManager.open
import org.bukkit.entity.Player
import java.util.*

class Choicer(
    override var uuid: UUID = UUID.randomUUID(),
    override var title: String = "Игра",
    var description: String = "Выбери нужный под-режим!",
    override var storage: MutableList<Button> = mutableListOf()
) : Storage {
    var allowClosing: Boolean = true

    constructor(title: String, description: String, vararg storage: Button) :
            this(UUID.randomUUID(), title, description, storage.toMutableList())

    companion object {

        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private val choicer: Choicer = Choicer()

        fun uuid(uuid: UUID) = apply { choicer.uuid = uuid }
        fun title(title: String) = apply { choicer.title = title }
        fun description(description: String) = apply { choicer.description = description }
        fun storage(storage: MutableList<Button>) = apply { choicer.storage = storage }
        fun storage(vararg storage: Button) = apply { choicer.storage = storage.toMutableList() }
        fun build() = choicer
    }

    override fun open(player: Player): Storage = open(player, "storage:choice") {
        string(description)
        boolean(allowClosing)
    }
}