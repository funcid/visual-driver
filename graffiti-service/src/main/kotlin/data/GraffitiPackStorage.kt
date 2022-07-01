package data

import actualGraffitiPacks
import kotlinx.serialization.Serializable
import me.func.protocol.Unique
import me.func.protocol.personalization.GraffitiPack
import me.func.protocol.util.UUIDSerializer
import java.util.UUID

@Serializable
data class GraffitiPackStorage(
    @Serializable(with = UUIDSerializer::class)
    override var uuid: UUID,
    var data: MutableList<GraffitiUnitStorage>,
) : Unique {

    // Метод для создания пака для отправки игроку имея только голые данные
    fun toFullData(): GraffitiPack? {
        actualGraffitiPacks[uuid]?.let { actual ->
            return actual.clone().apply {
                graffiti.forEach { graffiti ->
                    graffiti.uses = data.find { it.uuid == graffiti.uuid }?.uses ?: 0
                }
            }
        }
        return null
    }
}