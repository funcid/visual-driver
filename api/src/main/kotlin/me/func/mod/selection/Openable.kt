package me.func.mod.selection

import org.bukkit.entity.Player
import java.util.UUID

interface Openable {
    var uuid: UUID

    fun open(player: Player): Openable
}

