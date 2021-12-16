package me.func.protocol.graffiti

import me.func.protocol.Unique
import java.util.*

data class UserGraffitiData(
    private var uuid: UUID,
    var packs: MutableList<GraffitiPack>,
    var activePack: Int,
): Unique {
    override fun getUuid() = uuid
}