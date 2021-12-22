package me.func.protocol.graffiti

import me.func.protocol.Unique
import java.util.*

data class Graffiti(
    var address: GraffitiInfo,
    var author: String,
    var uses: Int = address.maxUses,
): Unique {
    constructor(uuid: String, x: Int, y: Int, size: Int, author: String, maxUses: Int = 50, uses: Int = maxUses) :
            this(GraffitiInfo(uuid, x, y, size, maxUses), author, uses)

    override fun getUuid(): UUID = address.getUuid()
}