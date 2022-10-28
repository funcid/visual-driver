package me.func.protocol.personalization

import me.func.protocol.Unique
import java.util.UUID

class GraffitiPack(
    override var uuid: UUID,
    var graffiti: MutableList<Graffiti>,
    var title: String,
    var creator: String,
    var price: Int,
    var rare: Int,
    var available: Boolean
) : Unique {
    fun clone() =
        GraffitiPack(uuid, graffiti.mapTo(ArrayList()) { it.clone() }, title, creator, price, rare, available)
}
