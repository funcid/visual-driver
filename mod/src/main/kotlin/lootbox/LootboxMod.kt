package lootbox

import dev.xdark.clientapi.event.input.MousePress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.event.window.WindowResize
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Easings
import ru.cristalix.uiengine.utility.V3

class LootboxMod {

    init {

        val crateScreen = CrateScreen()
        var ready = false
        var pressed = false

        mod.registerHandler<GameLoop> {
            if (!ready && !crateScreen.open)
                return@registerHandler

            if (crateScreen.open && Mouse.isButtonDown(0)) {
                crateScreen.open = false
                crateScreen.acceptClose()
                crateScreen.close()
                UIEngine.clientApi.minecraft().setIngameFocus()
                UIEngine.clientApi.clientConnection().sendPayload("lootbox:closed", Unpooled.EMPTY_BUFFER)
                return@registerHandler
            }

            if (ready && Mouse.isButtonDown(0) && !pressed) {
                pressed = true

                crateScreen.apply {
                    chest.animate(0.5, Easings.QUINT_OUT) {
                        chest.scale = V3(7.0, 7.0, 7.0)
                    }
                }
            }

            if (pressed) {
                clientApi.minecraft().setIngameNotInFocus()
                crateScreen.apply {
                    if (Mouse.isButtonDown(0)) shake()
                    else {
                        pressed = false
                        ready = false
                        acceptOpen()
                        if (hasNextItem()) {
                            UIEngine.schedule(0.5) {
                                pressed = false
                                ready = true
                            }
                        }
                    }
                }
            }
        }

        mod.registerHandler<MousePress> { isCancelled = ready }

        mod.registerHandler<PluginMessage> {
            if (channel == "lootbox") {
                val amount = data.readInt()

                val loot: MutableList<Loot> = arrayListOf()
                for (i in 0 until amount) {
                    val item = ItemTools.read(data)
                    val name = NetUtil.readUtf8(data)
                    val rarity = when (val rare = NetUtil.readUtf8(data)) {
                        "NOTHING" -> NOTHING
                        "COMMON" -> COMMON
                        "UNCOMMON" -> UNCOMMON
                        "RARE" -> RARE
                        "EPIC" -> EPIC
                        "LEGENDARY" -> LEGENDARY
                        "INCREDIBLE" -> INCREDIBLE
                        else -> Rarity(rare, RARE.color)
                    }
                    loot.add(Loot(item, name, rarity))
                }

                crateScreen.acceptClose()
                crateScreen.open()
                crateScreen.setup(loot)
                crateScreen.prepareToOpen()
                ready = true
            } else if (channel == "lootbox:close") {
                crateScreen.acceptClose()
                ready = false
                crateScreen.close()
                UIEngine.clientApi.minecraft().setIngameFocus()
                UIEngine.clientApi.clientConnection().sendPayload("lootbox:closed", Unpooled.EMPTY_BUFFER)
            }
        }

        mod.registerHandler<RenderTickPre> {
            if (!crateScreen.open)
                return@registerHandler

            crateScreen.apply {
                val intensity = rotationIntensity.color.alpha

                body1.animate(0.03) {
                    rotation.degrees = (Mouse.getX() / Display.getWidth().toDouble() - 0.5) * Math.PI / 2 * intensity
                }
                body2.animate(0.03) {
                    rotation.degrees = (Mouse.getY() / Display.getHeight().toDouble() - 0.5) * Math.PI / 2 * intensity
                }
                glowRect.animate(0.1) {
                    glowRect.rotation.degrees =
                        -(Mouse.getX() / Display.getWidth().toDouble() - 0.5) * Math.PI / 2 * intensity
                }
            }
        }

        mod.registerHandler<WindowResize> {
            crateScreen.background.size = UIEngine.overlayContext.size
            crateScreen.vignette.size = UIEngine.overlayContext.size
        }
    }
}
