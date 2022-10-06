package standard.storage.menu.daily

import Main.Companion.menuStack
import dev.xdark.clientapi.event.render.ScaleChange
import dev.xdark.clientapi.event.window.WindowResize
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.render.ScaledResolution
import io.netty.buffer.Unpooled
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.element.*
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import standard.storage.AbstractMenu
import standard.storage.button.StorageNode
import java.util.*

class DailyRewardMenu(
    override var uuid: UUID = UUID.randomUUID(),
    var currentDay: Int = 1,
    var taken: Boolean = false,
    override var storage: MutableList<StorageNode<*>> = arrayListOf()
) : AbstractMenu, ContextGui() {

    private var rootElement: RectangleElement? = null
    private var hoverContainer: CarvedRectangle? = null

    private var hover: CarvedRectangle? = null

    init {
        keyTypedHandlers.clear()
        onKeyTyped { _, code -> if (code == Keyboard.KEY_ESCAPE) closeGui() }

        color = Color(0, 0, 0, 0.86)

        update()
        updateScale()

        mod.registerHandler<WindowResize> { updateScale(resolution) }
        mod.registerHandler<ScaleChange> { updateScale() }
    }

    fun update() {

        rootElement?.let { removeChild(it) }

        val height = 189.5

        val hoverText = text {
            lineHeight = 12.0
            offset = V3(5.0, 5.0)
            shadow = true
        }

        val hoverCenter = carved {
            carveSize = 2.0
            align = CENTER
            origin = CENTER
            color = Color(54, 54, 54)

            +hoverText
        }

        hoverContainer = carved {
            carveSize = 2.0
            color = Color(75, 75, 75)

            enabled = false
            +hoverCenter

            beforeRender { GlStateManager.disableDepth() }
            afterRender { GlStateManager.enableDepth() }
        }

        rootElement = rectangle gui@{

            +backButton(height / 2 + 30)
            +closeButton(height / 2 + 30)

            size = V3(566.0, height)
            align = CENTER
            origin = CENTER

            var lastX = 0.0
            var lastY = 0.0

            Array(7) {
                val element = storage[it]
                val day = it + 1

                if (it == 3 || it == 6) {
                    lastX += 191.0
                    lastY = 0.0
                }

                val offsetX = if (it == 6) 0.0 else 6.5

                element.bundle = +carved {
                    carveSize = 3.0
                    size = V3(184.0, if (it == 6) 189.5 else 58.5)
                    align = TOP_LEFT
                    origin = TOP_LEFT
                    offset = V3(lastX, lastY)
                    color = Color(42, 102, 189, 0.28)

                    element.titleElement = +text {
                        content = "$day День"
                        align = if (it == 6) TOP else TOP_LEFT
                        origin = if (it == 6) TOP else TOP_LEFT
                        offset = V3(offsetX, 6.5)
                        scale = V3(1.925, 1.925, 1.925)
                        shadow = true
                    }

                    element.descriptionElement = +text {
                        align = if (it == 6) BOTTOM else BOTTOM_LEFT
                        origin = if (it == 6) BOTTOM else BOTTOM_LEFT
                        offset = V3(offsetX, -6.0)
                        scale = V3(0.925, 0.925, 0.925)
                        shadow = true
                    }

                    val icon = +rectangle {
                        size = V3(16.0, 16.0)
                        align = if (it == 6) CENTER else RIGHT
                        origin = if (it == 6) CENTER else RIGHT

                        offset = V3(if (it == 6) 0.0 else -20.0, 0.0)

                        +element.scaling(if (it == 6) 5.5 else 1.0).apply {
                            color = WHITE
                            align = CENTER
                            origin = CENTER
                            scale = if (it == 6) V3(5.5, 5.5, 1.0)
                            else V3(3.0, 3.0, 1.0)
                        }
                    }

                    onLeftClick {
                        if (day == currentDay && !taken) {
                            clientApi.clientConnection().sendPayload("func:reward:click", Unpooled.buffer().apply {
                                writeInt(day)
                            })

                            closeGui()
                        }
                    }

                    onHover {
                        val container = hoverContainer ?: return@onHover
                        val currentIcon = storage[day - 1].icon

                        container.enabled = hovered

                        animate(0.228, Easings.QUINT_OUT) {
                            if (hovered) {
                                currentIcon.scale = if (it == 6) V3(5.75, 5.75, 1.0)
                                else V3(3.25, 3.25, 1.0)
                            } else {
                                currentIcon.scale = if (it == 6) V3(5.5, 5.5, 1.0)
                                else V3(3.0, 3.0, 1.0)
                            }
                        }

                        if (hover == this@carved) return@onHover

                        if (hovered) {
                            hover = this@carved

                            val desc = element.title + "\n" + element.description
                            hoverText.content = desc

                            val lines = desc.split("\n")
                            val countLine = lines.size

                            container.size.x =
                                clientApi.fontRenderer().getStringWidth(lines.maxByOrNull { t -> t.length } ?: "")
                                    .toDouble() + 11.0
                            container.size.y = (hoverText.lineHeight * countLine) + 9.0

                            hoverCenter.size.x = container.size.x - 1.0
                            hoverCenter.size.y = container.size.y - 1.0
                        }
                    }
                }

                lastY += 65.5
            }
        }

        addChild(rootElement!!)
        addChild(hoverContainer!!)

        afterRender {
            clientApi.resolution().run {
                val mouseX = Mouse.getX()
                val mouseY = Mouse.getY()

                val displayHeight = Display.getHeight()

                val sizeX = mouseX / scaleFactor
                val sizeY = (displayHeight - mouseY) / scaleFactor

                val container = hoverContainer ?: return@afterRender

                container.offset.x = sizeX + 6.0
                container.offset.y = sizeY - 6.0
            }
        }
    }

    fun sendDayStatus() {
        for (i in 0..6) {
            val element = storage[i]
            val setDay = i + 1

            var color = Color(42, 102, 189)
            var content = "Награда за\nвход в игру"

            if (setDay < i) {
                color = Color(42, 102, 189, 0.62)
                content = "Награда получена!"
            }

            element.titleElement?.color = color
            element.descriptionElement?.content = content
        }

        var color = Color(42, 102, 189)
        var content = "Заберите награду\nза вход в игру"

        if (taken) {
            color = Color(42, 102, 189, 0.62)
            content = "Награда получена!"
        }

        storage[currentDay].titleElement?.color = color
        storage[currentDay].descriptionElement?.content = content
    }

    fun openGui() {
        if (clientApi.minecraft().currentScreen() != screen) open()
    }

    fun closeGui(hideWrapped: Boolean = true) {
        menuStack.clear()
        if (hideWrapped) close()
    }

    fun updateScale(resolution: ScaledResolution = clientApi.resolution()) {
        val rootElement = rootElement ?: return

        val factor = resolution.scaleFactor

        val scaleWidth = resolution.scaledWidth_double
        val scaleHeight = resolution.scaledHeight_double

        val sizeX = scaleWidth * factor / 2.0
        val sizeY = scaleHeight * factor / 2.0

        val highX = (sizeY / sizeX) * 100.0
        val highY = (sizeX / sizeY) * 100.0

        val scaleX = if (highX <= 100.0) sizeY else sizeX
        val scaleY = if (highY <= 100.0) sizeX else sizeY

        val scaleXY = (scaleX + scaleY) / 2.0
        val sizeXY = (sizeX + sizeY) / 2.0

        var scaleTotal = (((if (highX <= 47.257 || highY <= 47.257) scaleXY else sizeXY) * 0.001) + 0.26775)

        scaleTotal = when (factor) {
            1 -> scaleTotal * 2.0
            3 -> scaleTotal / 1.5
            4 -> scaleTotal / 2.0
            else -> scaleTotal
        }

        val rootSizeX = rootElement.size.x + 30.0
        val rootSizeY = rootElement.size.y + 30.0

        val percentX = (sizeX / rootSizeX) * 100.0
        val percentY = (sizeY / rootSizeY) * 100.0

        val scX = (1.0 * percentX) / 100.0
        val scY = (1.0 * percentY) / 100.0

        if ((sizeX <= rootSizeX || sizeY <= rootSizeY) && factor == 1) scaleTotal /= 1.73225 - (if (sizeX <= rootSizeX) scX else scY)

        size = V3(sizeX, sizeY)
        rootElement.scale = V3(scaleTotal, scaleTotal, 1.0)

        hoverContainer?.scale = V3(scaleTotal, scaleTotal, 1.0)
    }
}
