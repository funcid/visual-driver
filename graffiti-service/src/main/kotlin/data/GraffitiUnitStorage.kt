package data

import kotlinx.serialization.Serializable
import me.func.protocol.Unique
import me.func.protocol.util.UUIDSerializer
import java.util.UUID

@Serializable
data class GraffitiUnitStorage(
    var uses: Int = 0,
    @Serializable(with = UUIDSerializer::class)
    override var uuid: UUID
) : Unique