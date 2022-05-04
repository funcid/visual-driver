package me.func.protocol.personalization

import me.func.protocol.Unique
import java.util.UUID

class GraffitiInfo(
    override var uuid: UUID,
    var x: Int,
    var y: Int,
    var size: Int,
    var maxUses: Int,
) : Unique {
    constructor(uuid: String, x: Int, y: Int, size: Int, maxUses: Int) : this(
        UUID.fromString(uuid), x, y, size, maxUses
    )
}
