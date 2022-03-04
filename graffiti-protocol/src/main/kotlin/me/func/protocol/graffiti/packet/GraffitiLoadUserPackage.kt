package me.func.protocol.graffiti.packet

import me.func.protocol.personalization.FeatureUserData
import ru.cristalix.core.network.CorePackage
import java.util.*

data class GraffitiLoadUserPackage(
    var playerUuid: UUID, // request
    var data: FeatureUserData? = null // response
) : CorePackage()
