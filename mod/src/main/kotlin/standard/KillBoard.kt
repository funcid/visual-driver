package standard

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

class KillBoard {

    data class KillBoard(
        val text: String,
        val box: CarvedRectangle = carved {
            size = V3(UIEngine.clientApi.fontRenderer().getStringWidth(text) + 10.0, 12.0)
            color = Color(6, 6, 6, 0.5)
            offset = V3(10.0, 0.0)
            align = RIGHT
            + text {
                content = text
                align = CENTER
                shadow = true
                origin = V3(0.5, 0.5)
            }
        }
    )

    private var board = UIEngine.overlayContext + carved {
        size = V3(-12.0, 30.0)
        align = Relative.TOP_RIGHT
        origin = Relative.TOP_RIGHT
        offset = V3(-12.0, 15.0)
    }

    init {
        val minecraft = clientApi.minecraft()

        mod.registerHandler<GameLoop> {
            val inGame = minecraft.inGameHasFocus() && board.offset.y <= 20
            if (board.enabled && !inGame)
                board.enabled = false
            else if (!board.enabled && inGame)
                board.enabled = true
        }

        mod.registerChannel("func:notice") {
            val text = NetUtil.readUtf8(this)
            board.offset.y = readInt().toDouble()
            val notice = KillBoard(text)

            board + notice.box

            board.children[board.children.size - 1].animate(0.5) {
                offset.x = -this.size.x
            }

            board.children.reversed().forEachIndexed { index, current ->
                if (index == 6) {
                    current.animate(0.5) {
                        this.offset.x = this.size.x
                    }
                    UIEngine.schedule(0.5) {
                        board.removeChild(current)
                    }
                    board.removeChild(current)
                } else if (index != 0) {
                    current.animate(0.4) {
                        offset.y = (this.size.y + 5.0) * index
                    }
                    current.offset.x = -current.size.x
                }
            }

            UIEngine.schedule(10.0) {
                notice.box.animate(0.5) {
                    this.offset.x = this.size.x
                }
                UIEngine.schedule(0.5) {
                    board.removeChild(notice.box)
                }
            }
        }
    }
}
