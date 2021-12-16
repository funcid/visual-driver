package me.func.protocol.graffiti.packet

import me.func.protocol.packet.DataPackage
import java.util.*

data class GraffitiBuyPackage(
    val playerUUID: UUID, // request
    val packUUID: UUID, // request
    var price: Int, // request
    var errorMessage: String? = null // response
): DataPackage()