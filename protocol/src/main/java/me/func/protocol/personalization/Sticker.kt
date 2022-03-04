package me.func.protocol.personalization

import kotlinx.serialization.Serializable
import me.func.protocol.DropRare
import me.func.protocol.Unique
import me.func.protocol.util.UUIDSerializer
// import me.func.protocol.util.UUIDSerializer
import java.util.UUID

@Serializable
data class Sticker(
    @Serializable(with = UUIDSerializer::class)
    override val uuid: UUID,
    val name: String,
    val rare: DropRare,
    val openTime: Long
) : Unique
