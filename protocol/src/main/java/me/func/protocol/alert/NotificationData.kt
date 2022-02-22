package me.func.protocol.alert

import java.util.*

data class NotificationData(
    val source: UUID?,
    val type: String?,
    var content: String?,
    val timeoutBarColor: Int,
    val backgroundColor: Int,
    val timeout: Long,
    val buttons: List<NotificationButton>?,
    val chatMessage: String?
) : Cloneable {
    public override fun clone() =
        NotificationData(source, type, content, timeoutBarColor, backgroundColor, timeout, buttons, chatMessage)
}
