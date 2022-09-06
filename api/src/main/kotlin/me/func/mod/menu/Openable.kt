package me.func.mod.menu

import org.bukkit.entity.Player
import java.util.UUID

interface Openable {
    var uuid: UUID

    fun open(player: Player): Openable
}

