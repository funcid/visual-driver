import me.func.protocol.personalization.GraffitiPlaced
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.onMouseUp
import ru.cristalix.uiengine.utility.*
import java.util.UUID

data class LocalPack(
    var packUuid: UUID,
    val index: Int,
    val icon: RectangleElement = carved {
        origin = CENTER
        align = CENTER

        size = V3(ICON_PACK_SIZE, ICON_PACK_SIZE)
        color = WHITE

        onMouseUp {
            graffitiMod.gui.children.clear()
            graffitiMod.userData.activePack = index
            graffitiMod.loadPackIntoMenu()
        }
    },
    val title: TextElement = text {
        val pack = graffitiMod.getPack(packUuid)
        origin = CENTER
        align = CENTER
        color = WHITE
        offset.y -= 30
        shadow = true
        content = "${pack.title}\nby ${pack.creator}"
    }, var graffiti: List<LocalGraffiti> = graffitiMod.getPack(packUuid).graffiti.map { currentGraffiti ->
        LocalGraffiti(graffitiMod.getPack(packUuid), currentGraffiti, rectangle {
            origin = CENTER
            align = CENTER
            color = WHITE
            enabled = false

            textureLocation = graffitiMod.texture
            textureFrom = V3(currentGraffiti.address.x.toDouble() / PICTURE_SIZE, currentGraffiti.address.y.toDouble() / PICTURE_SIZE)
            size = V3(currentGraffiti.address.size.toDouble(), currentGraffiti.address.size.toDouble())
            textureSize =
                V3(currentGraffiti.address.size.toDouble() / PICTURE_SIZE, currentGraffiti.address.size.toDouble() / PICTURE_SIZE)

            onHover {
                var child = if (this@rectangle.children.isEmpty()) null else this@rectangle.children[0] as TextElement?

                if (child != null && !hovered) {
                    animate(0.1) {
                        scale.x = BASE_SCALE
                        scale.y = BASE_SCALE
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
                        scale.x = 0.3
                        scale.y = 0.3
                        color.alpha = 0.7
                    }
                }
            }
            onMouseUp {
                // Выбор граффити
                graffitiMod.gui.close()

                val player = UIEngine.clientApi.minecraft().player

                scale.x = 1.0
                scale.y = 1.0
                size = V3(75.0, 75.0)
                graffitiMod.activeGraffiti = LocalGraffitiPlaced(
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

                graffitiMod.activeGraffiti!!.context3D.addChild(this@rectangle)
                UIEngine.worldContexts.add(graffitiMod.activeGraffiti!!.context3D)
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