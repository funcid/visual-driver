package me.func.protocol

// import kotlinx.serialization.SerialName
// import kotlinx.serialization.Serializable
// import UUIDSerializer
import java.util.*

interface Unique {
    //@Serializable(with = UUIDSerializer::class)
    val uuid: UUID
}
