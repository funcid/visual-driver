package me.func.mod.ui.menu

import org.bukkit.entity.Player
import java.util.UUID

interface Openable {
    var uuid: UUID

    fun open(player: Player): Openable
}

