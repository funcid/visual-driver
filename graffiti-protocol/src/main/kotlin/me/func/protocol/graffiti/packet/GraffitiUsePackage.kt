package me.func.protocol.graffiti.packet

import ru.cristalix.core.network.CorePackage
import java.util.*

class GraffitiUsePackage(
    val playerUUID: UUID, // request
    val packUUID: UUID, // request
    val graffitiUUID: UUID, // request
    var success: Boolean = false // response
) : CorePackage()