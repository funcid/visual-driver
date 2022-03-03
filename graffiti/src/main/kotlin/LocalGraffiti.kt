import me.func.protocol.Graffiti
import me.func.protocol.GraffitiPack
import ru.cristalix.uiengine.element.RectangleElement

data class LocalGraffiti(
    var pack: GraffitiPack,
    var graffiti: Graffiti,
    var icon: RectangleElement,
) {
}