package me.func.protocol.personalization

import kotlinx.serialization.Serializable
import me.func.protocol.Unique
import me.func.protocol.util.UUIDSerializer
import java.util.UUID

@Serializable
data class FeatureUserData(
    @Serializable(with = UUIDSerializer::class)
    override var uuid: UUID,
    var packs: MutableList<GraffitiPack>,
    var activePack: Int,
    var stickers: MutableList<Sticker>,
    @Serializable(with = UUIDSerializer::class)
    var activeSticker: UUID?
) : Unique
