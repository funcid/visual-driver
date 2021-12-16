package me.func.protocol.graffiti

import me.func.protocol.Unique
import java.util.*

data class Graffiti(
    var address: GraffitiInfo,
    var uses: Int,
    var author: String,
): Unique {
    override fun getUuid(): UUID = address.getUuid()
}