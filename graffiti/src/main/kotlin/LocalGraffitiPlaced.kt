import me.func.protocol.personalization.GraffitiPlaced
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*

class LocalGraffitiPlaced(
    var graffiti: GraffitiPlaced,
    val container: Context3D,
    var indicator: CarvedRectangle = carved {
        align = LEFT
        origin = LEFT
        size = V3(60.0, 3.0)
        offset.z -= 0.01
        color = Color(42,102,189, 1.0)
    },
    var indicatorContainer: CarvedRectangle = carved {
        size = V3(60.0, 3.0)

        align = CENTER
        origin = CENTER

        offset.z -= 1
        offset.y += 21
        val scaled = 0.25
        scale = V3(scaled, scaled, scaled)
        offset.x += 10
        color = Color(0, 0, 0, 0.62)
        +indicator
    },
    var author: TextElement = text {
        align = CENTER
        origin = CENTER
        offset.z -= 1.002
        offset.y -= 2
        offset.x += 10
        val scaled = 0.25
        scale = V3(scaled, scaled, scaled)
        color = WHITE
        content = "Граффити от " + graffiti.ownerName
    },
    var authorShadow: TextElement = text {
        align = CENTER
        origin = CENTER
        offset.z -= 1
        offset.y -= 1.9
        offset.x += 10.2
        val scaled = 0.25
        scale = V3(scaled, scaled, scaled)
        color = BLACK
        content = "Граффити от " + graffiti.ownerName
    },
)