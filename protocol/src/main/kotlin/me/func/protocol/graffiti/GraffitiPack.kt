package me.func.protocol.graffiti

import me.func.protocol.Unique
import java.util.*

data class GraffitiPack(
    private var uuid: UUID,
    var graffiti: MutableList<Graffiti>,
    var title: String,
    var creator: String,
    var price: Int,
    var rare: Int,
    var available: Boolean
): Unique {
    override fun getUuid() = uuid
}
