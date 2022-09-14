package standard.storage.menu.selection

import Main.Companion.menuStack
import standard.storage.AbstractMenu
import standard.storage.TextedIcon
import standard.storage.button.StorageNode
import standard.storage.menu.MenuManager
import io.netty.buffer.Unpooled
import me.func.protocol.ui.menu.SelectionModel
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.UIEngine.clientApi
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.element.ItemElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.onMouseUp
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.math.ceil

class Selection(
    override var uuid: UUID,
    override var info: String = "",
    var title: String,
    var vault: String,
    @JvmField var money: String,
    @JvmField var hint: String,
    @JvmField var rows: Int,
    @JvmField var columns: Int,
    var pageCount: Int,
    var pages: MutableList<Page> = MutableList(pageCount) { Page(it) },
    override var storage: MutableList<StorageNode<*>> = mutableListOf(),
    @JvmField var currentPage: Int = 0,
) : AbstractMenu, ContextGui() {

    lateinit var arrowLeft: CarvedRectangle
    lateinit var arrowRight: CarvedRectangle

    private val width = 460.0
    private val height = 230.0
    private val padding = height / 12.0
    private val backButtonSize = 16.0
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
            +textWithMoney(money, vault).apply {
                origin = TOP_RIGHT
                align = TOP_RIGHT
                title.shadow = true
            }
        } else {
            menuTitle.origin = TOP
            menuTitle.align = TOP
        }
        if (menuStack.size > 0) {
            +carved {
                carveSize = 1.0
                align = BOTTOM
                origin = CENTER
                offset.y = backButtonSize / 2 - padding
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
        +grid.apply { size = V3(this@rectangle.size.x, this@rectangle.size.y - padding) }

        arrowRight = +drawChanger(false, ">")
        arrowLeft = +drawChanger(true, "<")

        onKeyTyped { _, code -> if (code == Keyboard.KEY_ESCAPE) menuStack.clear() }
    }

    private fun textWithMoney(text: String, vault: String, textLeft: Boolean = true) = TextedIcon(text, vault, textLeft)

    fun redrawGrid() {
        val elements = getElementsOnPage(currentPage)
        grid.children.clear()

        elements.forEach { element ->
            element.bundle?.let {
                grid + it
                return@forEach
            }
            grid + carved a@{
                val fieldHeight =
                    (height - (rows - 1) * flexSpace - padding * 2 - backButtonSize - menuTitle.lineHeight) / rows
                carveSize = 2.0
                size = V3((width - (columns - 1) * flexSpace) / columns, fieldHeight)
                color = if (element.special) Color(224, 118, 20, 0.28) else Color(21, 53, 98, 0.62)
                val image = +rectangle {
                    val iconSize = fieldHeight - MenuManager.itemPadding * 2
                    size = V3(iconSize, iconSize, iconSize)
                    origin = LEFT
                    align = LEFT
                    offset.x += MenuManager.itemPadding / 2 + 2
                    +element.scaling(iconSize).apply { color = WHITE }
                }
                val xOffset = image.size.x + MenuManager.itemPadding * 2
                +flex {
                    origin = TOP_LEFT
                    align = TOP_LEFT
                    offset.x = xOffset
                    offset.y = MenuManager.itemPadding + 1
                    flexDirection = FlexDirection.DOWN
                    flexSpacing = 0.0
                    element.titleElement = +text {
                        color = Color(255, 202, 66, 1.0)
                        scale = V3(0.75 + 0.125, 0.75 + 0.125, 0.75 + 0.125)
                        content = element.title
                        shadow = true
                        lineHeight = 8.0
                    }
                    element.descriptionElement = +text {
                        scale = V3(0.75 + 0.125, 0.75 + 0.125, 0.75 + 0.125)
                        content = element.description
                        shadow = true
                    }
                }
                if (element.price >= 0) {
                    val isItem = element.icon is ItemElement
                    val tag = if (isItem) (element.icon as ItemElement).stack?.tagCompound else null
                    val hasTag = isItem && tag?.hasKeyOfType("sale", 8) == true
                    val sale = if (isItem && hasTag) tag?.getString("sale")?.toInt() ?: 0 else 0
                    val price = element.price

                    +textWithMoney(
                        if (sale > 0) "§7§m$price§a ${(price * (100.0 - sale) / 100).toInt()} §c§l-$sale%" else price.toString(),
                        if (element.vault?.isEmpty() == true) vault else element.vault!!,
                        false
                    ).apply {
                        origin = BOTTOM_LEFT
                        align = BOTTOM_LEFT
                        title.shadow = true
                        offset.y -= MenuManager.itemPadding
                        offset.x = xOffset
                        scale = V3(0.75 + 0.125, 0.75 + 0.125, 0.75 + 0.125)
                    }
                }

                val hint = +element.createHint(this@a.size, hint)
                onHover {
                    val hasHoverEffect = hovered && !(element.hint.isNullOrEmpty() && this@Selection.hint.isEmpty())

                    animate(0.2, Easings.CUBIC_OUT) {
                        hint.color.alpha = if (hasHoverEffect) 0.95 else 0.0
                        hint.children[3].color.alpha = if (hasHoverEffect) 1.0 else 0.0
                    }
                }
                onMouseUp { element.click(this@Selection, this@onMouseUp) }
            }.apply {
                element.bundle = this
                element.optimizeSpace()
            }
        }
        arrowRight.enabled = currentPage < pageCount - 1
        arrowLeft.enabled = currentPage > 0
    }

    private fun getElementsOnPage(pageIndex: Int) = pages[pageIndex].content ?: listOf()

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
        onMouseUp {
            currentPage += if (left) -1 else 1
            val page = pages[currentPage]
            if (!page.isLoaded()) page.load(uuid) // пробуем загрузить страницу
            else redrawGrid()
        }
        +text {
            align = CENTER
            origin = CENTER
            color = WHITE
            content = text
        }
    }

    constructor(model: SelectionModel, pages: MutableList<Page>) : this(
        UUID.randomUUID(),
        model.info,
        model.title,
        model.vault,
        model.money,
        model.hint,
        model.rows,
        model.columns,
        ceil(model.data.size * 1.0 / model.rows * model.columns).toInt(),
        pages,
    )

    private val information = generateInformation()

    init {
        color = Color(0, 0, 0, .86)

        if (info.isNotEmpty()) +information

        redrawGrid()
    }

    override fun getInformationBlock() = information

}
