package me.func.protocol.alert

data class NotificationButton(
    val text: String? = null,
    val color: Int,
    val command: String? = null,
    val removeButton: Boolean,
    val removeNotification: Boolean) {

    fun replace(replacement: String) {
        if (text == null) return
        text.replace(text, replacement)
    }
}

