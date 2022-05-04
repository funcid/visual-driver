package me.func.protocol.alert

import java.util.*

class NotificationData(
    val source: UUID?,
    val type: String?,
    var content: String?,
    val timeoutBarColor: Int,
    val backgroundColor: Int,
    var timeout: Long,
    val buttons: List<NotificationButton>?,
    val chatMessage: String?
) : Cloneable {
    private var original: Boolean = true

    public override fun clone() = if (original) NotificationData(
        source,
        type,
        content,
        timeoutBarColor,
        backgroundColor,
        timeout,
        buttons,
        chatMessage
    ).apply { original = true } else this
}
