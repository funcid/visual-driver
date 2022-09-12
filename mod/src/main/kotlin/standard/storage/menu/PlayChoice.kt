package standard.storage.menu

import Main.Companion.menuStack
import dev.xdark.feder.NetUtil
import standard.storage.AbstractMenu
import standard.storage.button.StorageNode
import io.netty.buffer.Unpooled
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.onMouseUp
import ru.cristalix.uiengine.utility.*
import java.util.*

class PlayChoice(
    override var uuid: UUID,
    var title: String,
    @JvmField var description: String,
    allowClosing: Boolean,
    override var storage: MutableList<StorageNode<*>>
) : AbstractMenu, ContextGui() {
    init {
        if (!allowClosing) {
            keyTypedHandlers.removeFirstOrNull() // удаление листенера ESC перед регистрацией наших листенеров
        }

        val padding = 8.0
        val scaling = 0.85
        val buttonSize = V3(100.0, 145.0, 1.0)
        val iconSize = 70.0
        val backButtonSize = 20.0

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
            val flex = +flex {
                origin = CENTER
                align = CENTER
                flexSpacing = padding
                scale = V3(scaling, scaling, scaling)
                storage.forEachIndexed { index, element ->
                    element.bundle = +carved top@{
                        origin = CENTER
                        align = CENTER
                        size = buttonSize
                        color = if (element.special) centerColor else normal
                        element.titleElement = +text {
                            align = TOP
                            origin = TOP
                            color = if (element.special) textCenter else textNormal
                            content = element.title ?: ""
                            val mul = if (UIEngine.clientApi.fontRenderer()
                                    .getStringWidth(element.title) > buttonSize.x * scaling - 2 * padding
                            ) 0.75 else 1.0
                            scale = V3(1.0 / scaling * mul, 1.0 / scaling * mul, 1.0 / scaling * mul)
                            offset.y += padding * (1.0 / mul)
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
                                content = element.description ?: ""
                            }
                        }
                        onMouseUp { element.click(this@PlayChoice, this) }
                        carveSize = 2.0
                        val hint = +element.createHint(size, "Играть")
                        var hasHoverEffect = false

                        onHover {
                            val nowHovered = hovered && !element.hint.isNullOrEmpty()

                            if (nowHovered != hasHoverEffect) {
                                hasHoverEffect = nowHovered

                                animate(0.2, Easings.CUBIC_OUT) {
                                    hint.color.alpha = if (hasHoverEffect) 0.95 else 0.0
                                    element.hintElement?.color?.alpha = if (hasHoverEffect) 1.0 else 0.0
                                }
                            }
                        }
                    }

                    // element.optimizeSpace((element.bundle?.size?.x ?: 200.0) - 2 * padding)
                }
            }
        }
        if (menuStack.size > 0) {
            +carved {
                carveSize = 1.0
                align = CENTER
                origin = CENTER
                offset.y = buttonSize.y * scaling
                offset.x -= 65
                size = V3(40.0, backButtonSize)
                val normalColor = Color(42, 102, 189, 0.83)
                val hoveredColor = Color(224, 118, 20, 0.83)
                color = normalColor
                onHover {
                    animate(0.08, Easings.QUINT_OUT) {
                        color = if (hovered) hoveredColor else normalColor
                        scale = V3(if (hovered) 1.1 else 1.0, if (hovered) 1.1 else 1.0, 1.0)
                    }
                }
                onMouseUp {
                    menuStack.apply { pop() }.peek()?.open()
                    UIEngine.clientApi.clientConnection().sendPayload("func:back", Unpooled.EMPTY_BUFFER)
                }
                +text {
                    align = CENTER
                    origin = CENTER
                    color = WHITE
                    scale = V3(0.9, 0.9, 0.9)
                    content = "Назад"
                    shadow = true
                }
            }
        }
        if (allowClosing) {
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
                onMouseUp {
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

    init {
        color = Color(0, 0, 0, .86)
    }
}
