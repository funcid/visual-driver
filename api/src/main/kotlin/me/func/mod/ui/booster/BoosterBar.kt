package me.func.mod.ui.booster

import me.func.mod.conversation.ModTransfer
import me.func.protocol.ui.booster.CloseBoosterRequest
import me.func.protocol.ui.booster.OpenBoosterRequest
import org.bukkit.entity.Player

inline fun boosterBar(action: BoosterBar.() -> Unit) = BoosterBar().also(action)

inline fun boosterBarOpen(action: OpenBoosterRequest.() -> Unit) = OpenBoosterRequest().also(action)

inline fun boosterBarClose(action: CloseBoosterRequest.() -> Unit) = CloseBoosterRequest().also(action)

class BoosterBar(
    var segments: List<String>,
    var title: String,
    var subtitle: String,
    var isShowBackground: Boolean,
    var progress: Double
) {

    constructor() : this(listOf(), "", "", false, 1.0)

    fun open(player: Player) {
        val openBoosterRequest = boosterBarOpen {
            segments = this@BoosterBar.segments
            title = this@BoosterBar.title
            subtitle = this@BoosterBar.subtitle
            isShowBackground = this@BoosterBar.isShowBackground
            progress = this@BoosterBar.progress
        }

        ModTransfer().json(openBoosterRequest).send("boosterbar:open", player)
    }

    fun close(player: Player) {
        val closeBoosterRequest = boosterBarClose {
            isShowBackground = this@BoosterBar.isShowBackground
        }

        ModTransfer().json(closeBoosterRequest).send("boosterbar:close", player)
    }

}