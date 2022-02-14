package me.func.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.func.protocol.util.UUIDSerializer
import java.util.*

interface Unique {
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID
}
