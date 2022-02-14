package me.func.mod

import com.google.gson.Gson
import me.func.mod.conversation.ModTransfer
import me.func.protocol.alert.AlertColor
import me.func.protocol.alert.NotificationButton
import me.func.protocol.alert.NotificationData
import org.bukkit.entity.Player
import java.util.*


object Alert {

    fun send(player: Player, text: String, timeout: Long, alertColor: AlertColor, chatMessage: String, buttons: List<NotificationButton>) {
        if(buttons.size > 2) throw RuntimeException("Too many buttons!")

        var rgb: Int = alertColor.red
        rgb = (rgb shl 8) + alertColor.green
        rgb = (rgb shl 8) + alertColor.blue

        val notification = NotificationData(UUID.randomUUID(), "notify", text, rgb, rgb, timeout, buttons, chatMessage)
        val json = Gson().toJson(notification)

        ModTransfer()
            .json(json)
            .send("socials:notify", player)
    }

    fun createButton(alertType: AlertColor, text: String, command: String, removeButton: Boolean, removeNotification: Boolean): NotificationButton {
        var rgb: Int = alertType.red
        rgb = (rgb shl 8) + alertType.green
        rgb = (rgb shl 8) + alertType.blue

        return NotificationButton(text, rgb, command, removeButton, removeNotification)
    }
}