package me.func.protocol.graffiti

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.func.protocol.Unique
import me.func.protocol.util.UUIDSerializer
import java.util.UUID

@Serializable
data class UserGraffitiData(
    @SerialName("uuid")
    @Serializable(with = UUIDSerializer::class)
    override var uuid: UUID,
    var packs: MutableList<GraffitiPack>,
    var activePack: Int,
) : Unique
