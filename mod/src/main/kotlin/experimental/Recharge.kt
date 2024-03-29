package experimental

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

class Recharge {
    companion object {
        private lateinit var rechargeLine: CarvedRectangle
        private lateinit var rechargeContent: TextElement

        private var cooldown: RectangleElement? = null

        init {
            var time = 0.0
            var currentTime = System.currentTimeMillis()

            mod.registerHandler<GameLoop> {
                if (System.currentTimeMillis() - currentTime > 1000) {
                    time--
                    currentTime = System.currentTimeMillis()
                }
            }

            mod.registerChannel("func:recharge") {
                if (cooldown == null) {
                    cooldown = UIEngine.overlayContext + carved {
                        offset.y -= 65
                        origin = BOTTOM
                        align = BOTTOM
                        size = V3(180.0, 5.0, 0.0)
                        color = Color(0, 0, 0, 0.62)
                        rechargeLine = +carved {
                            origin = LEFT
                            align = LEFT
                            size = V3(180.0, 5.0, 0.0)
                            color = Color(42, 102, 189, 0.62)
                        }
                        rechargeContent = +text {
                            origin = TOP
                            align = TOP
                            color = WHITE
                            shadow = true
                            content = "Загрузка..."
                            offset.y -= 15
                        }
                        enabled = false
                    }
                }
                time = readDouble()
                val text = NetUtil.readUtf8(this)
                rechargeLine.color = Color(readInt(), readInt(), readInt(), 1.0)

                if (time == 0.0) {
                    rechargeLine.size.x = 0.0
                    cooldown?.enabled = false
                    return@registerChannel
                }

                cooldown?.enabled = true
                rechargeContent.content = text
                rechargeLine.animate(time - 0.1) {
                    size.x = 0.0
                }
                UIEngine.schedule(time) {
                    cooldown?.enabled = false
                    rechargeLine.size.x = 180.0
                }
            }
        }
    }
}
