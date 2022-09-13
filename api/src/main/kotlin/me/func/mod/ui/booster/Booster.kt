package me.func.mod.ui.booster

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player
import java.util.*

fun booster(booster: Booster.() -> Unit) = Booster().also(booster)

data class Booster(
    var uuid: UUID = UUID.randomUUID(),
    var enabled: Boolean = true,
    var name: String = "name",
    var multiplier: Double = 0.0
)

object Boosters {

    @JvmStatic
    fun send(player: Player, vararg boosters: Booster) = ModTransfer()
        .integer(boosters.size)
        .apply {
            boosters.forEach {
                uuid(it.uuid)
                boolean(it.enabled)
                string(it.name)
                double(it.multiplier)
            }
        }
        .send("mid:boosters", player)

    @JvmStatic
    fun mode(player: Player, enabled: Boolean) = ModTransfer()
        .boolean(enabled)
        .send("mid:boosters-mode", player)
}