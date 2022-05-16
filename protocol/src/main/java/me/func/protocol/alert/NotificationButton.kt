package me.func.protocol.alert

class NotificationButton(
    var text: String? = null,
    var color: Int,
    var command: String? = null,
    var removeButton: Boolean,
    var removeNotification: Boolean
) : Cloneable {
    public override fun clone() = NotificationButton(text, color, command, removeButton, removeNotification)
}
