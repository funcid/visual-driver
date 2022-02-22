package me.func.mod

import me.func.mod.Glow.toRGB
import me.func.mod.conversation.ModTransfer
import me.func.protocol.GlowColor
import me.func.protocol.alert.NotificationButton
import me.func.protocol.alert.NotificationData
import org.bukkit.entity.Player
import java.nio.charset.StandardCharsets
import java.util.*

object Alert {

    private val alertMap = hashMapOf<String, NotificationButton>()

    @JvmStatic
    fun send(player: Player, text: String, seconds: Long, frontColor: GlowColor, backGroundColor: GlowColor, chatMessage: String, vararg buttons: NotificationButton) {
        if(buttons.size > 2) throw RuntimeException("Too many buttons!")

        val notification = NotificationData(UUID.randomUUID(), "notify", text, toRGB(frontColor), toRGB(backGroundColor), seconds, buttons.asList(), chatMessage)
        ModTransfer()
            .byteArray(*ru.cristalix.core.GlobalSerializers.toJson(notification).toByteArray(StandardCharsets.UTF_8))
            .send("socials:notify", player)
    }

    @JvmStatic
    fun createButton(text: String, command: String, color: GlowColor, removeButton: Boolean, removeNotification: Boolean): NotificationButton =
        NotificationButton(text, toRGB(color), command, removeButton, removeNotification)

    @JvmStatic
    fun find(key: String) =  alertMap[key]!!

    @JvmStatic
    fun put(key: String, alert: NotificationButton) = alertMap.put(key, alert)
}