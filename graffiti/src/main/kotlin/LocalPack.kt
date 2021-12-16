import me.func.protocol.graffiti.GraffitiPlaced
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.WHITE
import ru.cristalix.uiengine.utility.rectangle
import java.util.*

data class LocalPack(
    var packUuid: UUID,
    var graffiti: List<LocalGraffiti> = app.getPack(packUuid).graffiti.map { currentGraffiti ->
        LocalGraffiti(app.getPack(packUuid), currentGraffiti, rectangle {
            origin = CENTER
            align = CENTER
            color = WHITE
            enabled = false

            textureLocation = app.texture
            textureFrom = V3(currentGraffiti.address.x.toDouble() / 1024, currentGraffiti.address.y.toDouble() / 1024)
            size = V3(currentGraffiti.address.size.toDouble(), currentGraffiti.address.size.toDouble())
            textureSize =
                V3(currentGraffiti.address.size.toDouble() / 1024, currentGraffiti.address.size.toDouble() / 1024)

            onHover {
                animate(0.1) {
                    scale.x = if (hovered) 1.15 else 1.0
                    scale.y = if (hovered) 1.15 else 1.0
                }
            }
            onClick {
                // Выбор граффити
                app.gui.close()

                val player = UIEngine.clientApi.minecraft().player

                app.activeGraffiti = LocalGraffitiPlaced(
                    GraffitiPlaced(
                        packUuid,
                        "world",
                        currentGraffiti,
                        player.x,
                        player.y,
                        player.z,
                        1000,
                    ), Context3D(V3(player.x, player.y, player.z).apply {
                        size = V3(20.0, 20.0)
                    })
                )
                size = V3(20.0, 20.0)
                scale = V3(1.0, 1.0)
                offset = V3(10.0, 10.0)
                color.alpha = 0.7

                app.activeGraffiti!!.context3D.addChild(this@rectangle)
                UIEngine.worldContexts.add(app.activeGraffiti!!.context3D)
            }
        })
    }
) {
    fun backGraffitiToPack(placed: LocalGraffitiPlaced) {
        val rectangle = placed.context3D.children[0]

        rectangle.size.x = placed.graffiti.graffiti.address.size.toDouble()
        rectangle.size.y = placed.graffiti.graffiti.address.size.toDouble()
        rectangle.color.alpha = 1.0

        placed.context3D.removeChild(rectangle)
        UIEngine.worldContexts.remove(placed.context3D)
    }
}