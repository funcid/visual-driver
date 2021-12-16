package me.func.protocol.graffiti

import me.func.protocol.Unique
import java.util.*

data class GraffitiInfo(
    private var uuid: UUID,
    var x: Int,
    var y: Int,
    var size: Int,
    var maxUses: Int,
): Unique {
    override fun getUuid() = uuid
}
