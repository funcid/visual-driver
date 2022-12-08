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
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.onMouseUp
import ru.cristalix.uiengine.utility.*
import standard.storage.AbstractMenu
import standard.storage.Information
import standard.storage.button.StorageNode
import java.awt.SystemColor.info
import java.util.*

class DailyRewardMenu(
    override var uuid: UUID = UUID.randomUUID(),
    var currentDay: Int = 0,
    var taken: Boolean = false,
    override var storage: MutableList<StorageNode<*>> = arrayListOf()
) : AbstractMenu, ContextGui() {

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

            if (menuStack.size > 0) +backButton(height / 2 + 30)
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

                    element.hoverText = element.title + if (element.description.isNotEmpty()) "\n" + element.description else ""

                    onMouseUp {
                        if (it == currentDay - 1 && !taken) {
                            clientApi.clientConnection().sendPayload("func:reward:click", Unpooled.buffer().apply {
                                writeInt(currentDay - 1)
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
                    }
                }

                lastY += 65.5
            }
        }

        addChild(rootElement!!)
    }

    fun sendDayStatus() {
        for (i in 0..6) {
            val element = storage[i]

            var color = Color(42, 102, 189, 0.28)
            var content = "Награда за\nвход в игру"

            if (i < currentDay) {
                color = Color(42, 102, 189, 0.62)
                content = "Награда получена!"
            }

            element.bundle?.color = color
            element.descriptionElement?.content = content
        }

        var color = Color(42, 102, 189)
        var content = "Собрать!"

        if (taken) {
            color = Color(42, 102, 189, 0.62)
            content = "Собрано!"
        }

        storage[currentDay - 1].bundle?.color = color
        storage[currentDay - 1].titleElement?.content = content
    }

    fun openGui() {
        if (clientApi.minecraft().currentScreen() != screen) open()
    }

    fun closeGui(hideWrapped: Boolean = true) {
        menuStack.clear()
        if (hideWrapped) close()
    }

    override fun close() = super.close(true)

}
