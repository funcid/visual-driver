package me.func.protocol.personalization

import me.func.protocol.Unique
import java.util.UUID

class FeatureUserData(
    override var uuid: UUID,
    var packs: MutableList<GraffitiPack>,
    var activePack: Int,
    var stickers: MutableList<Sticker>,
    var activeSticker: UUID?
) : Unique
