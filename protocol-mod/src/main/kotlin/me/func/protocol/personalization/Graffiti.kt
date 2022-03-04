package me.func.protocol.personalization

import me.func.protocol.Unique
import java.util.UUID

data class Graffiti(
    var address: GraffitiInfo,
    var author: String,
    var uses: Int = address.maxUses,
    override var uuid: UUID = address.uuid
) : Unique, Cloneable {
    constructor(uuid: String, x: Int, y: Int, size: Int, author: String, maxUses: Int = 50, uses: Int = maxUses) : this(
        GraffitiInfo(uuid, x, y, size, maxUses), author, uses
    )

    public override fun clone() = Graffiti(address, author, uses, uuid)
}