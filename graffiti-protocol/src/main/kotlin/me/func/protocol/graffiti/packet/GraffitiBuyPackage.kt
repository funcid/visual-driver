package me.func.protocol.graffiti.packet

import ru.cristalix.core.network.CorePackage
import java.util.UUID

data class GraffitiBuyPackage(
    val playerUUID: UUID, // request
    val packUUID: UUID, // request
    var price: Int, // request
    var errorMessage: String? = null // response
) : CorePackage()