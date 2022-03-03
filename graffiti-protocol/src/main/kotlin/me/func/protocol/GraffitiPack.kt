package me.func.protocol

// import kotlinx.serialization.Serializable
// import me.func.protocol.util.UUIDSerializer
import java.util.UUID

// @Serializable
data class GraffitiPack(
    // @Serializable(with = UUIDSerializer::class)
    override var uuid: UUID,
    var graffiti: MutableList<Graffiti>,
    var title: String,
    var creator: String,
    var price: Int,
    var rare: Int,
    var available: Boolean
) : Unique
