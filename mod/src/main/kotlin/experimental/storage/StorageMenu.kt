package experimental.storage

import backMenu
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import org.lwjgl.input.Mouse
import ru.cristalix.clientapi.JavaMod.loadTextureFromJar
import ru.cristalix.uiengine.UIEngine.clientApi
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.util.*

class StorageMenu(
    var uuid: UUID,
    var title: String,
    vault: String,
    var money: String,
    var hint: String,
    var rows: Int,
    var columns: Int,
    var storage: MutableList<StorageNode>,
) : ContextGui() {
    lateinit var arrowLeft: CarvedRectangle
    lateinit var arrowRight: CarvedRectangle

    private var page = 0
    private val coinLocation: ResourceLocation = loadTextureFromJar(clientApi, "icons", vault, "$vault.png")
    private val width = 460.0
    private val height = 230.0
    private val padding = height / 12.0
    private val backButtonSize = 16.0
    private val itemPadding = 4.0
    private val flexSpace = 3.5
    private val menuTitle = text {
        content = title
        shadow = true
        color = WHITE
        origin = TOP_LEFT
        align = TOP_LEFT
    }
    private var grid = flex {
        offset.y += padding + menuTitle.lineHeight
        origin = TOP
        align = TOP
        flexSpacing = flexSpace
        overflowWrap = true
    }
    private val container = +rectangle {
        align = CENTER
        origin = CENTER
        size = V3(width, height)

        +menuTitle
        if (money.isNotEmpty()) {
            +textWithMoney(money).apply {
                origin = TOP_RIGHT
                align = TOP_RIGHT
                title.shadow = true
            }
        }
        if (backMenu != this@StorageMenu && backMenu != null) {
            +carved {
                carveSize = 1.0
                align = BOTTOM
                origin = CENTER
                offset.y = backButtonSize / 2 - padding
                offset.x -= 65
                size = V3(40.0, backButtonSize)
                val normalColor = hex("2A66BD", 0.83)
                val hoveredColor = hex("E07614", 0.83)
                color = normalColor
                onHover {
                    animate(0.08, Easings.QUINT_OUT) {
                        color = if (hovered) hoveredColor else normalColor
                        scale = V3(if (hovered) 1.1 else 1.0, if (hovered) 1.1 else 1.0, 1.0)
                    }
                }
                onClick {
                    backMenu?.open()
                    clientApi.clientConnection().sendPayload("func:back", Unpooled.EMPTY_BUFFER)
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
        +carved {
            carveSize = 1.0
            align = BOTTOM
            origin = CENTER
            offset.y = backButtonSize / 2 - padding
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
                backMenu = null
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
        +grid.apply { size = V3(this@rectangle.size.x, this@rectangle.size.y - padding) }

        arrowRight = +drawChanger(false, ">")
        arrowLeft = +drawChanger(true, "<")
    }

    private fun textWithMoney(text: String, textLeft: Boolean = true) = TextedIcon(text, coinLocation, textLeft)

    private fun redrawGrid() {
        val elements = getElementsOnPage(page)
        grid.children.clear()

        elements.forEach { element ->
            grid + carved a@{
                val fieldHeight =
                    (height - (rows - 1) * flexSpace - padding * 2 - backButtonSize - menuTitle.lineHeight) / rows
                carveSize = 2.0
                size = V3((width - (columns - 1) * flexSpace) / columns, fieldHeight)
                color = Color(21, 53, 98, 0.62)
                val image = +rectangle {
                    val padding = fieldHeight - itemPadding * 2
                    size = V3(padding, padding, padding)
                    origin = LEFT
                    align = LEFT
                    offset.x += itemPadding / 2 + 2
                    +element.withPadding(padding).apply { color = WHITE }
                }
                val xOffset = image.size.x + itemPadding * 2
                +flex {
                    origin = TOP_LEFT
                    align = TOP_LEFT
                    offset.x = xOffset
                    offset.y = itemPadding + 1
                    flexDirection = FlexDirection.DOWN
                    flexSpacing = 0.0
                    +text {
                        color = Color(255, 202, 66, 1.0)
                        scale = V3(0.75 + 0.125, 0.75 + 0.125, 0.75 + 0.125)
                        content = element.title
                        shadow = true
                        lineHeight = 8.0
                    }
                    +text {
                        scale = V3(0.75 + 0.125, 0.75 + 0.125, 0.75 + 0.125)
                        if (element.price >= 0)
                            lineHeight = image.size.y - itemPadding * 2 - 10.0
                        content = element.description
                        shadow = true
                    }
                    if (element.price >= 0) {
                        +textWithMoney(element.price.toString(), false).apply {
                            title.shadow = true
                            scale = V3(0.75 + 0.125, 0.75 + 0.125, 0.75 + 0.125)
                        }
                    }
                }

                val hint = +carved {
                    carveSize = 2.0
                    size = this@a.size
                    color = Color(74, 140, 236, 1.0)
                    color.alpha = 0.0
                    beforeRender {
                        GlStateManager.disableDepth()
                    }
                    afterRender {
                        GlStateManager.enableDepth()
                    }

                    +text {
                        origin = CENTER
                        align = CENTER
                        color = WHITE
                        color.alpha = 0.0
                        content = hint
                        scale = V3(1.0, 1.0, 1.0)
                    }
                }
                onHover {
                    animate(0.2, Easings.CUBIC_OUT) {
                        hint.color.alpha = if (hovered) 0.95 else 0.0
                        hint.children[3].color.alpha = if (hovered) 1.0 else 0.0
                    }
                }
                onClick {
                    if (Mouse.isButtonDown(0)) {
                        clientApi.clientConnection().sendPayload("storage:click", Unpooled.buffer().apply {
                            NetUtil.writeUtf8(this, uuid.toString())
                            writeInt(storage.indexOf(element))
                        })
                    }
                }
            }
        }
        arrowRight.enabled = page < storage.size / getPageSize() - (if (storage.size % getPageSize() == 0) 1 else 0)
        arrowLeft.enabled = page > 0
    }

    private fun getPageSize() = rows * columns

    private fun getElementsOnPage(pageIndex: Int) = storage.drop(getPageSize() * pageIndex).take(getPageSize())

    private fun drawChanger(left: Boolean, text: String) = carved {
        carveSize = 1.0
        align = if (left) BOTTOM_LEFT else BOTTOM_RIGHT
        origin = CENTER
        offset.y = backButtonSize / 2 - padding
        offset.x += (if (left) -1.0 else 1.0) * (backButtonSize / 2 - padding)
        color = Color(42, 102, 189, 1.0)
        size = V3(backButtonSize, backButtonSize)
        val normalColor = Color(42, 102, 189, 0.83)
        val hoveredColor = Color(74, 140, 236, 0.83)
        color = normalColor
        onHover {
            animate(0.08, Easings.QUINT_OUT) {
                color = if (hovered) hoveredColor else normalColor
                scale = V3(if (hovered) 1.1 else 1.0, if (hovered) 1.1 else 1.0, 1.0)
            }
        }
        onClick {
            if (Mouse.isButtonDown(0)) {
                page += if (left) -1 else 1
                redrawGrid()
            }
        }
        +text {
            align = CENTER
            origin = CENTER
            color = WHITE
            content = text
        }
    }

    init {
        color = Color(0, 0, 0, 0.83)
        redrawGrid()
    }
}