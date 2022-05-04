package me.func.protocol.alert

class NotificationButton(
    var text: String? = null,
    val color: Int,
    val command: String? = null,
    val removeButton: Boolean,
    val removeNotification: Boolean
) : Cloneable {
    public override fun clone() = NotificationButton(text, color, command, removeButton, removeNotification)
}
