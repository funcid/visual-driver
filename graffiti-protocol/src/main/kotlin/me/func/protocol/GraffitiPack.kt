package me.func.protocol

import java.util.UUID

data class GraffitiPack(
    override var uuid: UUID,
    var graffiti: MutableList<Graffiti>,
    var title: String,
    var creator: String,
    var price: Int,
    var rare: Int,
    var available: Boolean
) : Unique
