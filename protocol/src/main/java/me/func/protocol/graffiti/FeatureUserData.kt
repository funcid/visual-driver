package me.func.protocol.graffiti

import kotlinx.serialization.Serializable
import me.func.protocol.Unique
import me.func.protocol.sticker.Sticker
import me.func.protocol.util.UUIDSerializer
import java.util.UUID

@Serializable
data class FeatureUserData(
    @Serializable(with = UUIDSerializer::class)
    override var uuid: UUID,
    var packs: MutableList<GraffitiPack>,
    var activeSticker: Sticker?,
    var stickers: MutableList<Sticker>,
    var activePack: Int
) : Unique {
    constructor(
        uuid: UUID,
        packs: MutableList<GraffitiPack>,
        stickers: MutableList<Sticker>,
        activePack: Int,
        activeSticker: Sticker?
    ) : this(uuid, packs, activeSticker, stickers, activePack)
}
