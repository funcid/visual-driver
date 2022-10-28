package me.func.protocol.ui.alert

import java.util.UUID

class NotificationData(
    var source: UUID?,
    var type: String?,
    var text: String?,
    var timeoutBarColor: Int,
    var backgroundColor: Int,
    var timeout: Long,
    var buttons: List<NotificationButton>?,
    var chatMessage: String?
) {
    private var original: Boolean = true

    fun clone() = if (original) NotificationData(
        source,
        type,
        text,
        timeoutBarColor,
        backgroundColor,
        timeout,
        buttons,
        chatMessage
    ).apply { original = true } else this
}
