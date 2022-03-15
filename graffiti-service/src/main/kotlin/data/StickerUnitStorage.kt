package data

import kotlinx.serialization.Serializable
import me.func.protocol.Unique
import me.func.protocol.util.UUIDSerializer
import java.util.*

@Serializable
data class StickerUnitStorage(
    @Serializable(with = UUIDSerializer::class)
    override val uuid: UUID,
    val openTime: Long
) : Unique