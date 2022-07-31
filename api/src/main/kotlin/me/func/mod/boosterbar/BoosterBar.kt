package me.func.mod.boosterbar

import me.func.mod.conversation.ModTransfer
import me.func.protocol.booster.bar.CloseBoosterRequest
import me.func.protocol.booster.bar.OpenBoosterRequest
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

class BoosterBarTest(val player: Player) {

    fun onJoin() {
        val boosterBar = boosterBar {
            segments = listOf()
            title = ""
            subtitle = ""
            isShowBackground = false
            progress = 1.0
        }

        boosterBar.open(player)
    }
}