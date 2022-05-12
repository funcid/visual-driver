package me.func.mod.selection

import org.bukkit.entity.Player
import java.util.*

interface Openable {

    fun open(player: Player): Openable

    var uuid: UUID

}