package me.func.protocol.alert

import java.util.*

data class NotificationData(
    val source: UUID?,
    val type: String?,
    val text: String?,
    val timeoutBarColor: Int,
    val backgroundColor: Int,
    val timeout: Long,
    val buttons: List<NotificationButton>?,
    val chatMessage: String?
)
