import me.func.protocol.personalization.GraffitiPlaced
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.onMouseUp
import ru.cristalix.uiengine.utility.*
import java.util.*

data class LocalPack(
    var packUuid: UUID,
    val index: Int,
    val icon: RectangleElement = rectangle {
        origin = CENTER
        align = CENTER

        val total = mod.getPack(packUuid).graffiti.first().address.size * 1.0 / PICTURE_SIZE
        textureLocation = mod.texture
        textureFrom = V3(0.0, total * index)
        textureSize = V3(total, total)
        size = V3(ICON_PACK_SIZE - 4, ICON_PACK_SIZE - 4)
        color = WHITE
    },
    val iconContainer: CarvedRectangle = carved {
        origin = CENTER
        align = CENTER
        size = V3(ICON_PACK_SIZE, ICON_PACK_SIZE)
        color = Color(0, 0, 0, 0.62)
        +icon
    },
    val title: TextElement = text {
        val pack = mod.getPack(packUuid)
        origin = CENTER
        align = CENTER
        color = WHITE
        offset.y -= 30
        shadow = true
        content = "${pack.title}\n${pack.creator}"
    }, var graffiti: List<LocalGraffiti> = mod.getPack(packUuid).graffiti.map { currentGraffiti ->
        LocalGraffiti(mod.getPack(packUuid), currentGraffiti, rectangle {
            origin = CENTER
            align = CENTER
            color = WHITE
            enabled = false

            textureLocation = mod.texture
            textureFrom = V3(
                currentGraffiti.address.x.toDouble() / PICTURE_SIZE,
                currentGraffiti.address.y.toDouble() / PICTURE_SIZE
            )
            size = V3(currentGraffiti.address.size.toDouble(), currentGraffiti.address.size.toDouble())
            textureSize =
                V3(
                    currentGraffiti.address.size.toDouble() / PICTURE_SIZE,
                    currentGraffiti.address.size.toDouble() / PICTURE_SIZE
                )

            this@rectangle.onHover {
                var child = if (this@rectangle.children.isEmpty()) null else this@rectangle.children[0] as TextElement?

                if (child != null && !hovered) {
                    this@rectangle.animate(0.1) {
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
                    this@rectangle.animate(0.1) {
                        scale.x = 0.3
                        scale.y = 0.3
                        color.alpha = 0.7
                    }
                }
            }
            onMouseUp {
                // Если граффити нет - не выбирать
                if (currentGraffiti.uses < 1) return@onMouseUp

                // Выбор граффити
                mod.gui.children.clear()
                mod.gui.close()

                val player = UIEngine.clientApi.minecraft().player

                scale.x = 1.0
                scale.y = 1.0
                size = V3(75.0, 75.0)
                mod.activeGraffiti = LocalGraffitiPlaced(
                    GraffitiPlaced(
                        packUuid,
                        player.name,
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

                mod.activeGraffiti!!.container.addChild(this@rectangle)
                UIEngine.worldContexts.add(mod.activeGraffiti!!.container)
            }
        })
    }
) {
    fun backGraffitiToPack(placed: LocalGraffitiPlaced) {
        val rectangle = placed.container.children[0]

        rectangle.size.x = placed.graffiti.graffiti.address.size.toDouble()
        rectangle.size.y = placed.graffiti.graffiti.address.size.toDouble()
        rectangle.color.alpha = 1.0

        placed.container.removeChild(rectangle)
        UIEngine.worldContexts.remove(placed.container)
    }
}