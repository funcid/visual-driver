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
    constructor(title: String, description: String, vararg storage: Button) :
            this(UUID.randomUUID(), title, description, storage.toMutableList())

    override fun open(player: Player): Storage = open(player, "storage:choice") { string(description) }
}