package me.func.mod

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player

object Shaders {

    fun loadShader(player: Player, type: Int, source: String) =
        ModTransfer()
            .string(source)
            .integer(type)
            .send("fiwka:shaders", player)
}