package me.func.protocol

import kotlinx.serialization.SerialName
import java.util.*

interface Unique {
    @SerialName("uuid")
    val uuid: UUID
}
