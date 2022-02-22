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

    private val alertTemplates = hashMapOf<String, NotificationData>()

    @JvmStatic
    fun send(
        player: Player,
        text: String,
        seconds: Long,
        frontColor: GlowColor,
        backGroundColor: GlowColor,
        chatMessage: String,
        vararg buttons: NotificationButton
    ) {
        if (buttons.size > 2) throw RuntimeException("Too many buttons!")

        NotificationData(
            UUID.randomUUID(),
            "notify",
            text,
            toRGB(frontColor),
            toRGB(backGroundColor),
            seconds,
            buttons.asList(),
            chatMessage
        ).send(player)
    }

    @JvmStatic
    fun button(
        message: String,
        command: String,
        color: GlowColor,
        removeButton: Boolean = false,
        removeNotification: Boolean = true
    ): NotificationButton =
        NotificationButton(message, toRGB(color), command, removeButton, removeNotification)

    @JvmStatic
    fun find(key: String) = alertTemplates[key]!!

    @JvmStatic
    fun put(key: String, alert: NotificationData) = alertTemplates.put(key, alert)

    // Метод для замены текста в сообщении
    fun NotificationData.replace(placeholder: String, content: String) =
        clone().apply { this.content?.let { this.content = this.content!!.replace(placeholder, content) } }

    // Метод для отправки готового сообщения игроку
    fun NotificationData.send(player: Player) = ModTransfer()
            .byteArray(*ru.cristalix.core.GlobalSerializers.toJson(this).toByteArray(StandardCharsets.UTF_8))
            .send("socials:notify", player)
}