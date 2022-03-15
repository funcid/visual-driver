package data

import kotlinx.serialization.Serializable
import me.func.protocol.Unique
import me.func.protocol.personalization.GraffitiPack
import me.func.protocol.personalization.Sticker
import me.func.protocol.util.UUIDSerializer
import java.util.*

@Serializable
data class FeatureUserStorage(
    @Serializable(with = UUIDSerializer::class)
    override var uuid: UUID,
    var packs: MutableList<GraffitiPackStorage>,
    var activePack: Int,
    var stickers: MutableList<Sticker>,
    @Serializable(with = UUIDSerializer::class)
    var activeSticker: UUID?
) : Unique