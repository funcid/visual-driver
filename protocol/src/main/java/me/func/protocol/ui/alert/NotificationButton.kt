package me.func.protocol.ui.alert

class NotificationButton(
    var text: String? = null,
    var color: Int,
    var command: String? = null,
    var removeButton: Boolean,
    var removeNotification: Boolean
) {
    fun clone() = NotificationButton(text, color, command, removeButton, removeNotification)
}
