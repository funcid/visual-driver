package standard.storage.menu

import Main.Companion.externalManager
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.gui.ingame.ChatScreen
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

class QueueStatus {
    companion object {
        private var buttonText: TextElement
        private var button: CarvedRectangle
        private var icon: CarvedRectangle
        private var png: RectangleElement
        private var title: TextElement
        private var desc: TextElement
        private var time: TextElement
        private var pattern: RectangleElement
        private const val width = 140.0
        private var counter = 0
        private var enabled = false

        private val container = carved {
            enabled = false
            color = Color(0, 0, 0, 0.62)
            size = V3(382.0 / 2.0, 100.0 / 2)
            align = TOP
            origin = TOP
            offset.y += -width + 15
            carveSize = 3.0
            icon = +carved {
                size = V3(100 / 2.0, 100.0 / 2)
                align = LEFT
                origin = LEFT
                carveSize = 3.0
                pattern = +rectangle {//Линия цвета
                    size = V3(20.0, 50.0)
                    offset.x = +30.0
                }
                png = +rectangle { //Иконка
                    size = V3(90 / 2.0, 90.0 / 2)
                    align = CENTER
                    origin = CENTER
                    color = WHITE
                }
                title = +text {//Название
                    align = TOP_LEFT
                    origin = TOP_LEFT
                    offset.x = +55.0
                    offset.y = +5.0
                    content = "Загрузка"
                    shadow = true
                }
                desc = +text {//Описание
                    align = TOP_LEFT
                    origin = TOP_LEFT
                    offset.x = +55.0
                    offset.y = +15.0
                    content = "Загрузка"
                    shadow = true
                }
                time = +text {//Время
                    align = BOTTOM_LEFT
                    origin = BOTTOM_LEFT
                    offset.x = +55.0
                    offset.y = -5.0
                    content = "⏰ 0:00"
                    shadow = true
                }
            }
            button = +carved {
                align = RIGHT
                origin = RIGHT
                offset.x -= 10
                size = V3(42.0 / 2, 68.0 / 2)
                color = Color(160, 29, 40, 0.83)
                carveSize = 2.0
                buttonText = +text {
                    align = CENTER
                    origin = CENTER
                    shadow = true
                    content = "X"
                }
                onLeftClick {
                    leave()
                    hide()
                }
                val normalColor = Color(160, 29, 40, 0.83)
                val hoveredColor = Color(231, 61, 75, 0.83)
                color = normalColor
                onHover {
                    animate(0.08) {
                        color = if (hovered) hoveredColor else normalColor
                    }
                }
            }
        }

        private fun leave() = UIEngine.clientApi.clientConnection().sendPayload("queue:leave", Unpooled.EMPTY_BUFFER)

        init {
            UIEngine.overlayContext + container
            var before = System.currentTimeMillis()

            mod.registerHandler<GameLoop> {
                if (!enabled)
                    return@registerHandler
                val now = System.currentTimeMillis()
                if (now - before > 1000) {
                    before = now
                    counter++
                    time.content = "⏰ ${counter / 60}:${(counter % 60).toString().padStart(2, '0')}"
                }
            }

            mod.registerChannel("queue:init") {
                val address = NetUtil.readUtf8(this) // texture
                val name = NetUtil.readUtf8(this) // title
                val description = NetUtil.readUtf8(this) // description
                if (!enabled) {
                    container.animate(0.4, Easings.BACK_OUT) {
                        offset.y = 15.0
                    }
                }

                png.textureLocation = externalManager.load(address)
                title.content = name
                desc.content = description
                container.enabled = true
                enabled = true
                mod.registerHandler<GameLoop> {
                    if (UIEngine.clientApi.minecraft().currentScreen() !is ChatScreen) {
                        container.enabled = enabled && !Keyboard.isKeyDown(Keyboard.KEY_TAB)
                    }
                }
            }

            mod.registerChannel("queue:update") {
                desc.content = NetUtil.readUtf8(this) // description
            }

            mod.registerChannel("queue:stop") {
                leave()
                hide()
            }
        }

        private fun hide() {
            container.animate(0.25, Easings.QUART_IN) {
                offset.y = -width + 15.0
            }

            UIEngine.schedule(0.26) {
                counter = 0
                container.enabled = false
                enabled = false
            }
        }
    }
}
