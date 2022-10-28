package me.func.mod.ui.booster

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player
import java.util.*

fun booster(booster: Booster.() -> Unit) = Booster().also(booster)

data class Booster(
    var name: String = "name",
    var multiplier: Double = 0.0
)

object Boosters {

    @JvmStatic
    fun send(player: Player, vararg boosters: Booster) = ModTransfer()
        .integer(boosters.size)
        .apply {
            boosters.forEach {
                string(it.name)
                double(it.multiplier)
            }
        }
        .send("zabelov:boosters", player)
}