package me.func.protocol

import me.func.protocol.sticker.Sticker
import java.util.UUID

data class FeatureUserData(
    override var uuid: UUID,
    var packs: MutableList<GraffitiPack>,
    var activePack: Int,
    var stickers: MutableList<Sticker>,
    var activeSticker: UUID?
) : Unique
