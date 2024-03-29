package battlepass

import dev.xdark.clientapi.event.render.ScaleChange
import dev.xdark.clientapi.event.window.WindowResize
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.resource.ResourceLocation
import io.netty.buffer.Unpooled
import me.func.protocol.data.rare.DropRare
import org.lwjgl.input.Mouse
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.writeUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.math.roundToInt

const val REWARDS_COUNT = 10

class BattlePassGui(
    var uuid: UUID,
    private val buyBlockText: String,
    private val price: Int,
    private val sale: Double,
    val pages: List<BattlePage> = listOf(),
    var quests: List<String> = listOf(),
    var claimed: MutableList<Int>
) : ContextGui() {
    @JvmField var isAdvanced: Boolean = false
    @JvmField var level: Int = 1
    @JvmField var exp: Int = 0
    @JvmField var requiredExp: Int = 1
    @JvmField var skipPrice: Int = 0
    @JvmField var lock = false

    private val guiSize = BattlePassGuiSize()

    private var battlepass: CarvedRectangle? = null
    private var moveLeftButton: CarvedRectangle? = null
    private var moveRightButton: CarvedRectangle? = null
    private var rewards: CarvedRectangle? = null

    init {
        color = Color(0, 0, 0, 0.86)

        update()

        mod.registerHandler<ScaleChange> { update() }
        mod.registerHandler<WindowResize> { update() }

        afterRender {
            val hoveredItem = hoveredReward ?: return@afterRender
            clientApi.resolution().run {
                val wholeDescription = hoveredItem.displayName
                screen.drawHoveringText(
                    wholeDescription, Mouse.getX() / scaleFactor,
                    (clientApi.resolution().scaledHeight_double * scaleFactor / scaleFactor - Mouse.getY() / scaleFactor).toInt()
                )
            }
        }
    }

    fun update() {
        if (battlepass != null) {
            removeChild(battlepass!!)
        }

        guiSize.calculate()
        battlepass = +carved main@{
            align = CENTER
            origin = CENTER
            size = V3(guiSize.totalWidth, guiSize.totalHeight)
            carveSize = 2.0

            +carved {
                align = TOP
                origin = TOP
                size = V3(guiSize.totalWidth, guiSize.totalHeightPart * 22.2)
                color = Color(226, 145, 25, 0.28)
                carveSize = 2.0

                val buyButtonNeed = !isAdvanced
                val skipButtonNeed = skipPrice != 0 && level < pages.size * REWARDS_COUNT

                if (buyButtonNeed) {
                    var approve = false

                    +carved buy@{
                        origin = LEFT
                        align = LEFT
                        size = V3(guiSize.buyButtonWidth, guiSize.buyButtonHeight)
                        color = Color(226, 145, 25, 1.0)
                        offset.x += guiSize.buyButtonOffsetX
                        carveSize = 2.0

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
                            if (lock)
                                return@onClick
                            if (!approve) {
                                approve = true
                                priceText.content = "Да, купить!"
                                color = Color(55, 200, 55, 1.0)
                                lock = true
                                UIEngine.schedule(0.2) { lock = false }
                            } else {
                                clientApi.clientConnection()
                                    .sendPayload("bp:buy-upgrade", Unpooled.buffer().apply {
                                        writeUtf8(uuid.toString())
                                    })
                                lock = false
                                close()
                            }
                        }
                        onHover {
                            animate(0.05) {
                                color = if (this@onHover.hovered) Color(244, 170, 61) else Color(226, 145, 25)
                            }
                            if (approve && !hovered) {
                                priceText.content = getPriceText(price)
                                color = Color(226, 145, 25)
                                approve = false
                            }
                            buyText.enabled = !hovered
                            priceText.enabled = !buyText.enabled
                        }
                    }
                }

                if (skipButtonNeed) {
                    var approve = false

                    +carved button@{
                        origin = LEFT
                        align = LEFT
                        size = V3(guiSize.buyButtonWidth, guiSize.buyButtonHeight)
                        carveSize = 2.0

                        color = Color(226, 145, 25, 1.0)
                        offset.x += if (buyButtonNeed) guiSize.totalWidthPart * 30 else guiSize.buyButtonOffsetX

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
                            if (approve && !hovered) {
                                priceText.content = getPriceText(skipPrice)
                                color = Color(226, 145, 25)
                                approve = false
                            }

                            animate(0.05) {
                                color = if (this@onHover.hovered) Color(244, 170, 61) else Color(226, 145, 25)
                            }

                            skipText.enabled = !hovered
                            priceText.enabled = !skipText.enabled
                        }

                        onClick {
                            if (!down) return@onClick
                            if (lock)
                                return@onClick
                            if (!approve) {
                                approve = true
                                priceText.content = "Да, купить!"
                                color = Color(55, 200, 55, 1.0)
                                lock = true
                                UIEngine.schedule(0.2) { lock = false }
                            } else {
                                clientApi.clientConnection()
                                    .sendPayload("bp:buy-page", Unpooled.buffer().apply {
                                        writeUtf8(uuid.toString())
                                        writeInt((skipPrice - skipPrice * sale / 100.0).toInt())
                                    })
                                lock = false
                                close()
                            }
                        }
                    }
                }

                +text {
                    origin = CENTER
                    align = CENTER
                    shadow = true
                    val buyBlockTextOffsetX = guiSize.totalWidthPart * 12.5
                    offset.x += buyBlockTextOffsetX * 2
                    content = " $buyBlockText"
                    lineHeight = 12.0
                    scale = V3(guiSize.totalWidthPart * 0.18, guiSize.totalWidthPart * 0.18)
                }
            }

            +carved {
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
                +carved progress@{
                    color = Color(24, 57, 105)

                    size = V3(guiSize.totalWidthPart * 88.24, guiSize.totalHeightPart * 3.8)
                    offset.x += progressLineOffsetX
                    offset.y += 1.0

                    +carved {
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

                content = if (quests.isEmpty()) "Все задания на сегодня выполнены!" else quests[0]


                if (quests.isNotEmpty()) {
                    var offsetY = guiSize.totalHeightPart * 4
                    quests.drop(1).forEach { quest ->
                        this@main.addChild(text {
                            align = BOTTOM_LEFT
                            origin = BOTTOM_LEFT
                            shadow = true
                            scale = V3(guiSize.totalWidthPart * 0.2, guiSize.totalWidthPart * 0.2)
                            offsetY -= guiSize.totalHeightPart * 5.0
                            offset.y -= offsetY
                            content = quest
                        })
                    }
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

    private fun addMoveButton(isToLeft: Boolean): CarvedRectangle = carved buttonMain@{
        size = V3(guiSize.totalWidthPart * 3.0, guiSize.rewardSizeY * 2.1)
        origin = if (isToLeft) TOP_LEFT else TOP_RIGHT
        align = if (isToLeft) TOP_LEFT else TOP_RIGHT
        offset.x = if (isToLeft) guiSize.totalWidthPart * -4 else guiSize.totalWidthPart * 4
        offset.y += guiSize.advancedOffsetY * 2.42
        color = Color(42, 102, 189, 0.28)
        carveSize = 2.0

        +text {
            offset.y -= 1
            align = CENTER
            origin = CENTER
            color = WHITE
            shadow = true
            content = if (isToLeft) "<" else ">"
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
        val normalColor = Color(42, 102, 189, 0.28)
        val hoveredColor = Color(74, 140, 236, 0.28)
        onHover {
            color = if (hovered) hoveredColor else normalColor
        }
    }.also { battlepass!!.addChild(it) }

    private var page = 0

    private fun addRewardRows(): CarvedRectangle = carved rewardMain@{
        align = CENTER
        origin = CENTER
        size = V3(guiSize.totalWidth, guiSize.totalHeightPart * 45.5)
        offset.y += guiSize.totalHeightPart * 22
        carveSize = 2.0

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
        repeat(REWARDS_COUNT) {
            +carved {
                size = V3(guiSize.totalWidthPart * 8.27, guiSize.totalHeightPart * 7.3)
                offset.x += offsetX
                offsetX += betweenX + (guiSize.totalWidthPart * 8.3)
                offset.y -= guiSize.totalHeightPart * 6.2
                carveSize = 2.0

                +text {
                    origin = CENTER
                    align = CENTER
                    content = (page * REWARDS_COUNT + it + 1).toString()
                    scale = V3(guiSize.totalWidthPart * 0.2, guiSize.totalWidthPart * 0.2)
                }
            }
        }
    }

    private fun addRewardRow(
        advanced: Boolean,
        firstBlockColor: Color,
        rewardBlockColor: Color,
        offsetY: Double
    ): CarvedRectangle = carved main@{
        color = firstBlockColor
        offset.y += offsetY
        size = V3(guiSize.rewardSizeX, guiSize.rewardSizeY)
        carveSize = 2.0

        +rectangle {
            size.x = guiSize.totalWidthPart * 3.18
            size.y = size.x
            align = CENTER
            origin = CENTER
            offset.y -= guiSize.totalHeightPart * 2
            color = WHITE
            textureLocation = ResourceLocation.of(
                "minecraft",
                if (advanced) "textures/items/emerald.png" else "textures/items/iron_ingot.png"
            )
        }
        +text {
            origin = BOTTOM
            align = BOTTOM
            shadow = true
            scale = V3(guiSize.totalWidthPart * 0.18, guiSize.totalWidthPart * 0.18)
            offset.y -= guiSize.totalHeightPart * 3.4
            content = if (advanced) "Премиум" else "Базовый"
        }

        var offsetX = guiSize.rewardSizeX + guiSize.rewardBetweenX

        (if (advanced) pages[page].advancedItems else pages[page].items).forEachIndexed { index, it ->
            var currentRare = DropRare.COMMON
            var taken = false

            it?.tagCompound?.let {
                it.getInteger("rare").let { currentRare = DropRare.values()[it] }
                taken = if (it.hasKey("taken")) it.getBoolean("taken") else false
            }

            taken =
                taken || claimed.contains(page * REWARDS_COUNT + index + if (advanced) pages.size * REWARDS_COUNT else 0)

            val canTake = index + page * REWARDS_COUNT < level && !taken && (!advanced || isAdvanced)

            +carved rewardMain@{
                color =
                    if (taken) Color(235, 66, 66, 0.28) else if (canTake) Color(59, 193, 80, 0.28) else rewardBlockColor
                size = V3(guiSize.rewardBlockWidth, guiSize.totalHeightPart * 18.9)
                offset.x += offsetX
                offsetX += guiSize.rewardBetweenX + guiSize.rewardBlockWidth
                carveSize = 2.0

                +item {
                    origin = CENTER
                    align = CENTER
                    stack = it!!
                    scale =
                        V3(guiSize.totalWidthPart * 0.34, guiSize.totalWidthPart * 0.34, guiSize.totalWidthPart * 0.34)
                }
                +carved {
                    align = BOTTOM_LEFT
                    origin = BOTTOM_LEFT
                    size = V3(this@rewardMain.size.x, guiSize.totalHeightPart * 0.87)
                    color = Color(currentRare.red, currentRare.green, currentRare.blue)
                    carveSize = 2.0
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
                    if (!canTake) return@onClick

                    color = Color(235, 66, 66, 0.28)

                    clientApi.clientConnection().sendPayload("bp:reward", Unpooled.buffer().apply {
                        writeBoolean(advanced)
                        writeInt(page)
                        writeInt(index)

                        claimed.add(page * REWARDS_COUNT + index + if (advanced) pages.size * REWARDS_COUNT else 0)
                    })
                }
            }
        }
    }

    private var hoveredReward: ItemStack? = null

    private fun getPriceText(price: Int): String {
        if (sale == 0.0) return "§f§l$price кристалов"
        return "§c§l§m$price§f§l ${(price - price * sale / 100.0).roundToInt()} кристалов"
    }
}
