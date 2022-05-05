import me.func.protocol.personalization.Graffiti
import me.func.protocol.personalization.GraffitiPack
import ru.cristalix.uiengine.element.RectangleElement

data class LocalGraffiti(
    var pack: GraffitiPack,
    var graffiti: Graffiti,
    var icon: RectangleElement,
)
