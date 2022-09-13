package me.func.mod.ui

import me.func.mod.conversation.ModTransfer
import me.func.mod.util.warn
import me.func.protocol.data.color.RGB
import me.func.protocol.ui.alert.NotificationButton
import me.func.protocol.ui.alert.NotificationData
import org.bukkit.entity.Player
import java.nio.charset.StandardCharsets
import java.util.UUID

object Alert {
    private val alertTemplates = hashMapOf<String, NotificationData>()

    @JvmStatic
    fun send(
        player: Player,
        text: String,
        millis: Long,
        frontColor: RGB,
        backGroundColor: RGB,
        chatMessage: String,
        vararg buttons: NotificationButton
    ) {
        if (buttons.size > 2) {
            warn("Too many buttons!")
            return
        }

        NotificationData(
            UUID.randomUUID(),
            "notify",
            text,
            frontColor.toRGB(),
            backGroundColor.toRGB(),
            millis,
            buttons.asList(),
            chatMessage
        ).send(player)
    }

    @JvmStatic
    fun button(
        message: String,
        command: String,
        color: RGB,
        removeButton: Boolean = false,
        removeNotification: Boolean = true
    ): NotificationButton =
        NotificationButton(message, color.toRGB(), command, removeButton, removeNotification)

    @JvmStatic
    fun find(key: String) = alertTemplates[key]!!

    @JvmStatic
    fun put(key: String, alert: NotificationData) = alertTemplates.put(key, alert)

    // Метод для замены текста в сообщении
    @JvmStatic
    @JvmName("notificationDataReplace")
    fun NotificationData.replace(placeholder: String, content: String) =
        clone().apply { this.text?.let { this.text = this.text!!.replace(placeholder, content) } }

    // Метод для отправки готового сообщения игроку
    @JvmStatic
    @JvmName("sendNotificationData")
    fun NotificationData.send(player: Player) = ModTransfer()
        .byteArray(*ru.cristalix.core.GlobalSerializers.toJson(this).toByteArray(StandardCharsets.UTF_8))
        .send("socials:notify", player)
}
