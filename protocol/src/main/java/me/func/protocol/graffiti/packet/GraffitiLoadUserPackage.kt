package me.func.protocol.graffiti.packet

import me.func.protocol.graffiti.UserGraffitiData
import me.func.protocol.packet.DataPackage
import java.util.*

data class GraffitiLoadUserPackage(
    var playerUuid: UUID, // request
    var data: UserGraffitiData? = null // response
) : DataPackage()