package me.func.protocol.graffiti

// import kotlinx.serialization.Serializable
import me.func.protocol.Unique
// import me.func.protocol.util.UUIDSerializer
import java.util.UUID

// @Serializable
data class Graffiti(
    var address: GraffitiInfo,
    var author: String,
    var uses: Int = address.maxUses,
    // @Serializable(with = UUIDSerializer::class)
    override var uuid: UUID = address.uuid
) : Unique {
    constructor(uuid: String, x: Int, y: Int, size: Int, author: String, maxUses: Int = 50, uses: Int = maxUses) : this(
        GraffitiInfo(uuid, x, y, size, maxUses), author, uses
    )
}
