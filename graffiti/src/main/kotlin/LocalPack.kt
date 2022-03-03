import me.func.protocol.GraffitiPlaced
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.util.*

data class LocalPack(
    var packUuid: UUID,
    val index: Int,
    val icon: RectangleElement = rectangle {
        origin = CENTER
        align = CENTER

        val boxSize = ICON_PACK_SIZE
        val boxOpposite = 8

        offset.y += OVAL_RADIUS - 10
        offset.x += index * boxSize + index * boxOpposite - app.userData.packs.size * (boxSize - boxOpposite) / 2 - boxOpposite

        size = V3(boxSize, boxSize)
        color = WHITE

        onClick {
            app.gui.children.clear()
            app.userData.activePack = index
            app.loadPackIntoMenu()
        }
    },
    val title: TextElement = text {
        val pack = app.getPack(packUuid)
        origin = CENTER
        align = CENTER
        color = WHITE
        offset.y -= 30
        shadow = true
        content = "${pack.title}\nby ${pack.creator}"
    }, var graffiti: List<LocalGraffiti> = app.getPack(packUuid).graffiti.map { currentGraffiti ->
        LocalGraffiti(app.getPack(packUuid), currentGraffiti, rectangle {
            origin = CENTER
            align = CENTER
            color = WHITE
            enabled = false

            textureLocation = app.texture
            textureFrom = V3(currentGraffiti.address.x.toDouble() / PICTURE_SIZE, currentGraffiti.address.y.toDouble() / PICTURE_SIZE)
            size = V3(currentGraffiti.address.size.toDouble(), currentGraffiti.address.size.toDouble())
            textureSize =
                V3(currentGraffiti.address.size.toDouble() / PICTURE_SIZE, currentGraffiti.address.size.toDouble() / PICTURE_SIZE)

            onHover {
                var child = if (this@rectangle.children.isEmpty()) null else this@rectangle.children[0] as TextElement?

                if (child != null && !hovered) {
                    animate(0.1) {
                        scale.x = 0.25
                        scale.y = 0.25
                        color.alpha = 1.0
                    }
                    removeChild(child!!)
                } else if (child == null && hovered) {
                    child = text {
                        origin = CENTER
                        align = CENTER
                        color = WHITE
                        scale.x = 4.0
                        scale.y = 4.0
                        shadow = true
                        content = "${currentGraffiti.uses} штук"
                    }
                    addChild(child!!)
                    animate(0.1) {
                        scale.x = 0.37
                        scale.y = 0.37
                        color.alpha = 0.7
                    }
                }
            }
            onClick {
                // Выбор граффити
                app.gui.close()

                val player = UIEngine.clientApi.minecraft().player

                scale.x = 1.0
                scale.y = 1.0
                size = V3(75.0, 75.0)
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
                        size = V3(65.0, 65.0)
                        color = WHITE
                    })
                )
                offset = V3(10.0, 10.0)

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