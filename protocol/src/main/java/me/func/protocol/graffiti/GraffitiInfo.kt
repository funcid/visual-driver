package me.func.protocol.graffiti

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.func.protocol.Unique
import me.func.protocol.util.UUIDSerializer
import java.util.UUID

@Serializable
data class GraffitiInfo(
    @SerialName("uuid")
    @Serializable(with = UUIDSerializer::class)
    override var uuid: UUID,
    var x: Int,
    var y: Int,
    var size: Int,
    var maxUses: Int,
) : Unique {
    constructor(uuid: String, x: Int, y: Int, size: Int, maxUses: Int) : this(
        UUID.fromString(uuid), x, y, size, maxUses
    )
}
