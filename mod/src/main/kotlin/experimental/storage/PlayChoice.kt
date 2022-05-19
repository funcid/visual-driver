package experimental.storage

import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import menuStack
import java.util.*

class PlayChoice(
    override var uuid: UUID,
    override var title: String,
    var description: String,
    override var storage: MutableList<StorageNode<*>>
) : Storable(uuid, title, storage) {
    init {
        val padding = 8.0
        val scaling = 0.7
        val buttonSize = V3(100.0, 145.0, 1.0)
        val iconSize = 70.0

        val title = text {
            origin = TOP
            align = TOP
            scale = V3(1.75, 1.75, 1.75)
            content = this@PlayChoice.title
        }
        val description = text {
            origin = TOP
            align = TOP
            offset.y += padding * 2 + title.lineHeight
            scale = V3(0.865, 0.865, 0.865)
            content = this@PlayChoice.description
        }
        val container = +rectangle {
            origin = CENTER
            align = CENTER
            size = V3(UIEngine.overlayContext.size.x, 240.0)
            val centerColor = Color(224, 118, 20, 0.28)
            val normal = Color(42, 102, 189, 0.28)
            val textNormal = Color(74, 140, 236, 1.0)
            val textCenter = Color(255, 157, 66, 1.0)
            val iconHover = Color(255, 255, 200, 1.0)
            +title
            +description
            +flex {
                origin = CENTER
                align = CENTER
                flexSpacing = padding
                scale = V3(scaling, scaling, scaling)
                storage.forEachIndexed { index, element ->
                    val centered = storage.size / 2 == index && storage.size % 2 == 1
                    element.bundle = +carved top@{
                        size = buttonSize
                        color = if (centered) centerColor else normal
                        element.titleElement = +text {
                            align = TOP
                            origin = TOP
                            color = if (centered) textCenter else textNormal
                            content = element.title
                            scale = V3(1 / scaling, 1 / scaling, 1 / scaling)
                            offset.y += padding
                        }
                        val icon = +rectangle {
                            size = V3(iconSize, iconSize, iconSize)
                            align = TOP
                            origin = TOP
                            offset.y += padding * 2 + element.titleElement!!.lineHeight
                            +element.scaling(iconSize).apply { color = WHITE }
                        }
                        val lore = +rectangle {
                            align = BOTTOM
                            origin = BOTTOM
                            size = V3(
                                buttonSize.x - 2 * padding,
                                buttonSize.y - 2 * padding - iconSize - element.titleElement!!.lineHeight * scaling
                            )
                            element.descriptionElement = +text {
                                align = CENTER
                                origin = CENTER
                                lineHeight += padding / 2
                                content = element.description
                            }
                        }
                        onClick {
                            if (Mouse.isButtonDown(0)) {
                                UIEngine.clientApi.clientConnection().sendPayload("storage:click", Unpooled.buffer().apply {
                                    NetUtil.writeUtf8(this, uuid.toString())
                                    writeInt(storage.indexOf(element))
                                })
                            }
                        }
                        carveSize = 2.0
                        val hint = +element.createHint(size, "Играть")
                        onHover {
                            animate(0.2, Easings.CUBIC_OUT) {
                                hint.color.alpha = if (hovered) 0.95 else 0.0
                                hint.children[3].color.alpha = if (hovered) 1.0 else 0.0
                            }
                        }
                    }

                    element.optimizeSpace((element.bundle?.size?.x ?: 200.0) - 2 * padding)
                }
            }
        }

        val backButtonSize = 20.0
        +carved {
            carveSize = 1.0
            align = CENTER
            origin = CENTER
            offset.y = buttonSize.y * scaling
            size = V3(76.0, backButtonSize)
            val normalColor = Color(160, 29, 40, 0.83)
            val hoveredColor = Color(231, 61, 75, 0.83)
            color = normalColor
            onHover {
                animate(0.08, Easings.QUINT_OUT) {
                    color = if (hovered) hoveredColor else normalColor
                    scale = V3(if (hovered) 1.1 else 1.0, if (hovered) 1.1 else 1.0, 1.0)
                }
            }
            onClick {
                close()
                menuStack.clear()
            }
            +text {
                align = CENTER
                origin = CENTER
                color = WHITE
                scale = V3(0.9, 0.9, 0.9)
                content = "Выйти [ ESC ]"
                shadow = true
            }
        }
        onKeyTyped { _, code -> if (code == Keyboard.KEY_ESCAPE) menuStack.clear() }
    }
}