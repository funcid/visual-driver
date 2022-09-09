package me.func.mod

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player

fun booster(booster: Booster.Booster.() -> Unit) = Booster.Booster().also(booster)

object Booster {
    data class Booster(
        var name: String = "name",
        var multiplier: Double = 0.0
    )

    @JvmStatic
    fun startBoosters(player: Player, enable: Boolean, vararg boosters: Booster) = ModTransfer()
        .integer(boosters.size)
        .boolean(enable)
        .apply {
            boosters.forEach {
                string(it.name)
                double(it.multiplier)
            }
        }.send("mid:boost", player)
}