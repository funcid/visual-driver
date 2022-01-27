import dev.xdark.clientapi.event.render.ScaleChange
import dev.xdark.clientapi.event.window.WindowResize
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.resource.ResourceLocation
import io.netty.buffer.Unpooled
import me.func.protocol.DropRare
import org.lwjgl.input.Mouse
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.PI
import kotlin.math.roundToInt

class BattlePassGui(
    private val buyBlockText: String,
    private val price: Int,
    private val sale: Double,
    val pages: List<BattlePage> = listOf(),
    var quests: List<String> = listOf(),
) : ContextGui() {

    var isAdvanced: Boolean = false
    var level: Int = 1
    var exp: Int = 0
    var requiredExp: Int = 1
    var skipPrice: Int = 0

    private val guiSize = BattlePassGuiSize()

    private var battlepass: RectangleElement? = null
    private var moveLeftButton: RectangleElement? = null
    private var moveRightButton: RectangleElement? = null
    private var rewards: RectangleElement? = null
    private val rewardsCount = 10

    init {
        color = Color(0, 0, 0, 0.86)

        update()

        registerHandler<ScaleChange> { update() }
        registerHandler<WindowResize> { update() }

        afterRender {
            val hoveredItem = hoveredReward ?: return@afterRender
            JavaMod.clientApi.resolution().run {
                val wholeDescription = hoveredItem.displayName
                screen.drawHoveringText(
                    wholeDescription, Mouse.getX() / scaleFactor,
                    (scaledHeight_double * scaleFactor / scaleFactor - Mouse.getY() / scaleFactor).toInt()
                )
            }
        }
    }

    fun update() {
        if(battlepass != null) {
            removeChild(battlepass!!)
        }

        guiSize.calculate()
        battlepass = +rectangle {
            align = CENTER
            origin = CENTER
            size = V3(guiSize.totalWidth, guiSize.totalHeight)

            +rectangle {
                align = TOP
                origin = TOP
                size = V3(guiSize.totalWidth, guiSize.totalHeightPart * 22.2)
                color = Color(226, 145, 25, 0.28)

                val buyButtonNeed = !isAdvanced
                val skipButtonNeed = skipPrice != 0

                if (buyButtonNeed) {
                    +rectangle buy@{
                        origin = LEFT
                        align = LEFT
                        size = V3(guiSize.buyButtonWidth, guiSize.buyButtonHeight)
                        color = Color(226, 145, 25, 1.0)
                        offset.x += guiSize.buyButtonOffsetX

                        val buyText = +text {
                            align = CENTER
                            origin = CENTER
                            shadow = true
                            content = "Купить"
                            scale = V3(guiSize.totalWidthPart * 0.2, guiSize.totalWidthPart * 0.2)
                        }

                        val priceText = +text {
                            enabled = false
                            align = CENTER
                            origin = CENTER
                            shadow = true
                            content = getPriceText(price)
                            scale = V3(guiSize.totalWidthPart * 0.2, guiSize.totalWidthPart * 0.2)
                        }

                        onClick {
                            close()
                            JavaMod.clientApi.clientConnection().sendPayload("bp:buy-upgrade", Unpooled.buffer())
                        }
                        onHover {
                            animate(0.05) {
                                color = if (this@onHover.hovered) Color(244, 170, 61) else Color(226, 145, 25)
                            }
                            buyText.enabled = !hovered
                            priceText.enabled = !buyText.enabled
                        }
                    }
                }

                if(skipButtonNeed) {
                    +rectangle button@{
                        origin = LEFT
                        align = LEFT
                        size = V3(guiSize.buyButtonWidth, guiSize.buyButtonHeight)

                        color = Color(226, 145, 25, 1.0)
                        offset.x += if(buyButtonNeed) guiSize.totalWidthPart * 30 else guiSize.buyButtonOffsetX

                        val skipText = +text {
                            align = CENTER
                            origin = CENTER
                            shadow = true
                            content = "Пропустить"
                            scale = V3(guiSize.totalWidthPart * 0.2, guiSize.totalWidthPart * 0.2)
                        }

                        val priceText = +text {
                            enabled = false
                            align = CENTER
                            origin = CENTER
                            shadow = true
                            content = getPriceText(skipPrice)
                            scale = V3(guiSize.totalWidthPart * 0.2, guiSize.totalWidthPart * 0.2)
                        }

                        onHover {
                            animate(0.05) {
                                color = if (this@onHover.hovered) Color(244, 170, 61) else Color(226, 145, 25)
                            }

                            skipText.enabled = !hovered
                            priceText.enabled = !skipText.enabled
                        }

                        onClick {
                            if (!down) return@onClick
                            close()
                            JavaMod.clientApi.clientConnection().sendPayload("bp:buy-page", Unpooled.buffer())
                        }
                    }
                }

                +text {
                    origin = CENTER
                    align = CENTER
                    shadow = true
                    val buyBlockTextOffsetX = guiSize.totalWidthPart * 12.5
                    offset.x += if(buyButtonNeed) {
                        if(skipButtonNeed) buyBlockTextOffsetX * 2 else buyBlockTextOffsetX - guiSize.buyButtonWidth
                    } else {
                        if(skipButtonNeed) buyBlockTextOffsetX - guiSize.buyButtonWidth else buyBlockTextOffsetX - (guiSize.buyButtonWidth * 2)
                    }
                    content = " $buyBlockText"
                    lineHeight = 12.0
                    scale = V3(guiSize.totalWidthPart * 0.18, guiSize.totalWidthPart * 0.18)
                }
            }

            +rectangle {
                align = CENTER
                origin = CENTER
                size = V3(guiSize.totalWidth, guiSize.totalHeightPart * 22.2)

                offset.y -= guiSize.totalHeightPart * 10.8

                +text {
                    shadow = true
                    lineHeight = 10.0
                    offset.x += guiSize.totalWidthPart * 1.8
                    content = "Уровень\n    $level"
                    scale = V3(guiSize.totalWidthPart * 0.2, guiSize.totalWidthPart * 0.2)
                }

                val progressLineOffsetX = guiSize.totalWidthPart * 11.9
                +rectangle progress@{
                    color = Color(24, 57, 105)

                    size = V3(guiSize.totalWidthPart * 88.24, guiSize.totalHeightPart * 3.8)
                    offset.x += progressLineOffsetX
                    offset.y += 1.0

                    +rectangle {
                        val progress = if (requiredExp != 0) exp.toDouble() / requiredExp.toDouble() else 1.0
                        size = V3(this@progress.size.x * progress, this@progress.size.y)
                        color = Color(42, 102, 189, 1.0)
                    }
                }

                +text {
                    shadow = true
                    content = "Опыт: $exp из $requiredExp"
                    offset.x += progressLineOffsetX
                    offset.y += guiSize.totalHeightPart * 4.6
                    scale = V3(guiSize.totalWidthPart * 0.2, guiSize.totalWidthPart * 0.2)
                }
            }

            +text {
                align = BOTTOM_LEFT
                origin = BOTTOM_LEFT
                shadow = true

                offset.y -= guiSize.totalHeightPart * 4

                scale = V3(guiSize.totalWidthPart * 0.2, guiSize.totalWidthPart * 0.2)

                content = if (quests.isEmpty()) " Все задания на сегодня выполнены!" else {
                    " Задания:\n${quests.joinToString(separator = "\n")}"
                }
            }
        }

        rewards = addRewardRows()
        battlepass!! + rewards!!
        moveLeftButton = addMoveButton(true)
        moveRightButton = addMoveButton(false)
        updateMoveButtonsVisibility()
    }

    private fun updateMoveButtonsVisibility() {
        moveLeftButton!!.enabled = page > 0
        moveRightButton!!.enabled = page < pages.size - 1
    }

    private fun addMoveButton(isToLeft: Boolean): RectangleElement = rectangle buttonMain@{
        size = V3(guiSize.totalWidthPart * 3.0, guiSize.rewardSizeY * 2.1)
        origin = if (isToLeft) TOP_LEFT else TOP_RIGHT
        align = if (isToLeft) TOP_LEFT else TOP_RIGHT
        offset.x = if (isToLeft) -20.0 else 23.0
        offset.y += guiSize.advancedOffsetY * 2.42
        color = Color(42, 102, 189, 0.28)

        +rectangle {
            size.x = this@buttonMain.size.x * 2 / 3
            size.y = size.x

            offset.y -= 1

            align = CENTER
            origin = CENTER
            color = Color(255, 255, 255)
            textureLocation = ResourceLocation.of("minecraft", "textures/gtm/arrow.png")

            if (isToLeft) rotation.degrees = -PI
        }

        onClick {
            if (enabled && down) {
                page += if (isToLeft) -1 else 1

                battlepass!!.removeChild(rewards!!)
                rewards = addRewardRows()
                battlepass!!.addChild(rewards!!)

                updateMoveButtonsVisibility()
            }
        }
    }.also { battlepass!!.addChild(it) }

    private var page = 0

    private fun addRewardRows(): RectangleElement = rectangle rewardMain@{
        align = CENTER
        origin = CENTER
        size = V3(guiSize.totalWidth, guiSize.totalHeightPart * 45.5)
        offset.y += guiSize.totalHeightPart * 22

        +addRewardRow(
            false,
            Color(124, 124, 124, 0.62),
            Color(124, 124, 124, 0.28),
            0.0
        )
        +addRewardRow(
            true,
            Color(226, 132, 44, 0.62),
            Color(185, 122, 27, 0.28),
            guiSize.advancedOffsetY
        )

        val betweenX = guiSize.rewardBetweenX
        var offsetX = (guiSize.totalWidthPart * 11.45) + betweenX
        repeat(rewardsCount) {
            +rectangle {
                size = V3(guiSize.totalWidthPart * 8.27, guiSize.totalHeightPart * 7.3)
                offset.x += offsetX
                offsetX += betweenX + (guiSize.totalWidthPart * 8.3)
                offset.y -= guiSize.totalHeightPart * 6.2

                +text {
                    origin = CENTER
                    align = CENTER
                    content = (page * rewardsCount + it + 1).toString()
                    scale = V3(guiSize.totalWidthPart * 0.2, guiSize.totalWidthPart * 0.2)
                }
            }
        }
    }

    private fun addRewardRow(
        isAdvanced: Boolean,
        firstBlockColor: Color,
        rewardBlockColor: Color,
        offsetY: Double
    ): RectangleElement = rectangle main@{
        color = firstBlockColor
        offset.y += offsetY
        size = V3(guiSize.rewardSizeX, guiSize.rewardSizeY)

        +rectangle {
            size.x = guiSize.totalWidthPart * 3.18
            size.y = size.x
            align = CENTER
            origin = CENTER
            offset.y -= guiSize.totalHeightPart * 2
            color = WHITE
            textureLocation = ResourceLocation.of("minecraft", "textures/gui/kirka.png")
        }
        +text {
            origin = BOTTOM
            align = BOTTOM
            shadow = true
            scale = V3(guiSize.totalWidthPart * 0.18, guiSize.totalWidthPart * 0.18)
            offset.y -= guiSize.totalHeightPart * 3.4
            content = if (isAdvanced) "Премиум" else "Базовый"
        }

        var offsetX = guiSize.rewardSizeX + guiSize.rewardBetweenX

        (if (isAdvanced) pages[page].advancedItems else pages[page].items).forEachIndexed { index, it ->
            var currentRare = DropRare.COMMON
            var taken = false

            it?.tagCompound?.let {
                it.getInteger("rare").let { currentRare = DropRare.values()[it] }
                taken = if (it.hasKey("taken")) it.getBoolean("taken") else false
            }

            +rectangle rewardMain@{
                color = if(taken) Color(34, 174, 73, 0.72) else rewardBlockColor
                size = V3(guiSize.rewardBlockWidth, guiSize.totalHeightPart * 18.9)
                offset.x += offsetX
                offsetX += guiSize.rewardBetweenX + guiSize.rewardBlockWidth

                +item {
                    origin = CENTER
                    align = CENTER
                    stack = it!!
                    scale = V3(guiSize.totalWidthPart * 0.34, guiSize.totalWidthPart * 0.34, guiSize.totalWidthPart * 0.34)
                }
                +rectangle {
                    align = BOTTOM_LEFT
                    origin = BOTTOM_LEFT
                    size = V3(this@rewardMain.size.x, guiSize.totalHeightPart * 0.87)
                    color = Color(currentRare.red, currentRare.green, currentRare.blue)
                }

                onHover {
                    if (hovered) {
                        hoveredReward = it
                    } else if (hoveredReward == it) {
                        hoveredReward = null
                    }
                }

                onClick {
                    if (!down || taken) return@onClick
                    close()
                    JavaMod.clientApi.clientConnection().sendPayload("bp:reward", Unpooled.buffer().apply {
                        writeInt(index + page * rewardsCount)
                        writeBoolean(isAdvanced)
                    })
                }
            }
        }
    }

    private var hoveredReward: ItemStack? = null

    private fun getPriceText(price: Int): String {
        if (sale == 0.0) return "§f§l$price кристалов"
        return "§c§l§m$price§f§l ${(price * sale / 100.0).roundToInt()} кристалов"
    }

}