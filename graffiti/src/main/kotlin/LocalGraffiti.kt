import me.func.protocol.graffiti.Graffiti
import me.func.protocol.graffiti.GraffitiPack
import ru.cristalix.uiengine.element.RectangleElement

data class LocalGraffiti(
    var pack: GraffitiPack,
    var graffiti: Graffiti,
    var icon: RectangleElement,
) {
}