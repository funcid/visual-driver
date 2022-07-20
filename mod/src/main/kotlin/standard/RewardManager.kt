package standard

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.feder.NetUtil
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.onMouseUp
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.WHITE
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.text

class RewardManager {
    lateinit var hintText: TextElement
    private var currentDay = 0
    private var hint: RectangleElement? = null

    init {
        val box = rectangle {
            size = V3(2000.0, 2000.0)
            color = Color(0, 0, 0, 0.86)
            origin = CENTER
            align = CENTER
            enabled = false
        }
        val week = arrayListOf<Day>()
        val gui = ContextGui()
        gui + box

        mod.registerChannel("func:weekly-reward") {
            if (box.enabled) return@registerChannel
            currentDay = readInt()

            val topText = text {
                origin = CENTER
                align = CENTER
                color = WHITE
                shadow = true
                scale = V3(1.5, 1.5)
                offset.y -= 110.0
                content = "Ваша ежедневная награда / $currentDay день"
            }

            gui + topText

            for (i in 0..6) {
                val dayBox = Day(
                    i + 1, ItemTools.read(this), NetUtil.readUtf8(this),
                    when {
                        currentDay > i + 1 -> "§7СОБРАНО"
                        currentDay < i + 1 -> "§eСКОРО"
                        else -> "§f§lВЗЯТЬ"
                    }, i + 1 < currentDay
                )
                val topElement = dayBox.children[0] as TextElement
                when {
                    currentDay > i + 1 -> topElement.content = "§7" + topElement.content
                    currentDay < i + 1 -> topElement.content = "§b" + topElement.content
                    else -> {
                        topElement.content = "§lУРА!\nнаграда"
                        dayBox.color = Color(224, 118, 20, 0.3)
                        dayBox.onMouseUp {
                            gui.removeChild(
                                box,
                                topElement,
                                hint!!,
                                *week.toTypedArray(),
                                topText
                            )
                            clientApi.chat().sendChatMessage("/lootboxsound")
                            gui.close()
                        }
                    }
                }

                dayBox.onHover {
                    if (hovered) {
                        hintText.content = dayBox.name
                        hint?.enabled = true
                        hint?.size?.x = hintText.size.x + 4
                        week.forEach {
                            if (it != this) {
                                it.normalize()
                                it.animate(0.1) {
                                    size.x = 50.0
                                    size.y = 150.0
                                }
                            }
                            UIEngine.schedule(0.1) {
                                if (it != dayBox) {
                                    it.move(it.day.compareTo(dayBox.day))
                                }
                            }
                        }
                        animate(0.1) {
                            size.x = 55.0
                            size.y = 175.0
                        }
                    } else {
                        hint?.enabled = false
                        week.forEach {
                            if (it != dayBox) {
                                it.normalize()
                            }
                        }
                        animate(0.1) {
                            size.x = 50.0
                            size.y = 150.0
                        }
                    }
                }
                week.add(dayBox)
                gui + dayBox
            }
            box.enabled = true
            gui.open()
        }

        mod.registerHandler<GameLoop> {
            val scale = clientApi.resolution().scaleFactor
            hint?.offset?.x = (Mouse.getX() / scale).toDouble()
            hint?.offset?.y = ((Display.getHeight() - Mouse.getY()) / scale).toDouble()
        }

        hint = gui + rectangle {
            color = Color(0, 0, 0, 0.7)
            size.x = 100.0
            size.y = 14.0
            offset.z += 10
            enabled = false
            hintText = +text {
                content = "???"
                offset.x = 2.0
                offset.y = 2.0
            }
        }
    }
}
