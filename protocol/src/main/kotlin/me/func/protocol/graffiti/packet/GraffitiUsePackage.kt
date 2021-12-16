package me.func.protocol.graffiti.packet

import me.func.protocol.packet.DataPackage
import java.util.*

class GraffitiUsePackage(
    val playerUUID: UUID, // request
    val packUUID: UUID, // request
    val graffitiUUID: UUID, // request
    var boolean: Boolean = false // response
) : DataPackage()