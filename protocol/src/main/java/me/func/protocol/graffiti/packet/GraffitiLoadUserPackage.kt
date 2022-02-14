package me.func.protocol.graffiti.packet

import me.func.protocol.graffiti.FeatureUserData
import me.func.protocol.packet.DataPackage
import java.util.*

data class GraffitiLoadUserPackage(
    var playerUuid: UUID, // request
    var data: FeatureUserData? = null // response
) : DataPackage()
