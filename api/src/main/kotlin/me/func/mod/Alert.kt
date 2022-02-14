package me.func.mod

import me.func.mod.conversation.ModTransfer
import me.func.protocol.GlowColor
import me.func.protocol.alert.AlertColor
import me.func.protocol.alert.NotificationButton
import me.func.protocol.alert.NotificationData
import org.bukkit.entity.Player
import java.util.*


object Alert {

    @JvmStatic
    private fun toRGB(alertColor: GlowColor): Int {
        var rgb: Int = alertColor.red
        rgb = (rgb shl 8) + alertColor.green
        rgb = (rgb shl 8) + alertColor.blue
        return rgb
    }

    @JvmStatic
    fun send(player: Player, text: String, seconds: Long, frontColor: GlowColor, backGroundColor: GlowColor, chatMessage: String, vararg buttons: NotificationButton) {
        if(buttons.size > 2) throw RuntimeException("Too many buttons!")

        val notification = NotificationData(UUID.randomUUID(), "notify", text, toRGB(frontColor), toRGB(backGroundColor), seconds, buttons.asList(), chatMessage)
        ModTransfer()
            .json(notification)
            .send("socials:notify", player)
    }

    @JvmStatic
    fun createButton(text: String, command: String, color: GlowColor, removeButton: Boolean, removeNotification: Boolean): NotificationButton {

        return NotificationButton(text, toRGB(color), command, removeButton, removeNotification)
    }
}