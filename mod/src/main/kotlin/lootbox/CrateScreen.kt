package lootbox

import dev.xdark.clientapi.item.Item
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.opengl.GlStateManager
import dev.xdark.clientapi.opengl.RenderHelper
import dev.xdark.clientapi.resource.ResourceLocation
import io.netty.buffer.Unpooled
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.UIEngine.clientApi
import ru.cristalix.uiengine.element.ItemElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.BOTTOM
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.Easings
import ru.cristalix.uiengine.utility.Rotation
import ru.cristalix.uiengine.utility.V2
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.WHITE
import ru.cristalix.uiengine.utility.cube
import ru.cristalix.uiengine.utility.item
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.text

class CrateScreen {
    var opened = false

    val rotationIntensity = rectangle {
        color.alpha = 0.6
    }

    val background = rectangle {
        size = UIEngine.overlayContext.size
        color = Color(0, 0, 0, 0.86)
        enabled = false
        beforeRender = {
            //GL11.glDepthMask(false)
            GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT)
        }
    }

    private val text = text {
        scale = V3(2.0, 2.0)
        align = V3(0.5, 0.9)
        origin = BOTTOM
        color.alpha = 0.0
    }

    val vignette = rectangle {
        size = UIEngine.overlayContext.size

        color = PURPLE
        color.alpha = 0.0

        textureLocation = ResourceLocation.of("minecraft", "textures/misc/vignette.png")
        beforeRender = {
            GlStateManager.disableDepth()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        }

        afterRender = {
            GlStateManager.enableDepth()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        }
    }

    val glowRect = rectangle {
        rotation.y = 1.0
        rotation.z = 0.0

        align = V3(0.5, 0.5, 0.5)
        origin = CENTER
        size = V3(300.0, 300.0)
        color = PURPLE
        color.alpha = 0.85

        textureLocation = ResourceLocation.of("minecraft", "textures/others/glow.png")
        beforeRender = {
            GlStateManager.disableDepth()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        }

        afterRender = {
            GlStateManager.enableDepth()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        }
    }

    private val rarityText = text {
        scale = V3(2.0, 2.0, 1.0)
        offset.z = 30.0
        offset.y = -100.0
        align = CENTER
        origin = CENTER
        content = "Эпическая награда"
        color = PURPLE
        color.alpha = 0.0
    }

    private val nameText = text {
        offset.z = 30.0
        offset.y = 45.0
        align = CENTER
        origin = CENTER
        content = "Тотем\nбессмертия"
        color.alpha = 0.0
    }

    private val itemRect = rectangle {
        offset.z = 30.0
        offset.y = 20.0
        align = CENTER
        origin = CENTER
        color = Color(0x80, 0x3C, 0xEE, 0.28)
        size = V3(23.0, 23.0)
        scale = V3(2.5, 2.5, 2.5)
        addChild(rectangle {
            origin = BOTTOM
            align = BOTTOM
            size = V3(23.0, 1.0)
            color = Color(0x80, 0x3C, 0xEE, 1.0)
            beforeRender = {
                GlStateManager.disableDepth()
            }
            afterRender = {
                GlStateManager.enableDepth()
            }
        })
        addChild(item {
            offset.z = -150.0
            stack = ItemStack.of(Item.of(449), 1, 0)
            align = CENTER
            origin = CENTER
            beforeRender = {
                GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT)
            }
        })
    }

    private val chestLid = cube {
        rotation.x = 1.0
        rotation.z = 0.0

        size = V3(14.0, 5.0, 14.0)

        align = V3(0.5, 0.5 / 10.0, 1.0 - 0.5 / 14.0)
        origin = V3(0.5, 1.0 - 0.5 / 5.0, 1.0 - 0.5 / 14.0)

        color = WHITE
        textureLocation =
            clientApi.resourceManager().getLocation("minecraft", "textures/entity/chest/normal.png")
        textureSize = V3(64.0, 64.0)

        addChild(cube {

            align = V3(0.5, 0.0, 0.0)
            origin = V3(0.5, 0.0, 1.0)
            offset.y = 3.0

            color = WHITE
            size = V3(2.0, 4.0, 1.0)

            textureLocation =
                clientApi.resourceManager().getLocation("minecraft", "textures/entity/chest/normal.png")
            textureSize = V3(64.0, 64.0)

        })
    }

    val chest = cube {
        align = V3(0.5, 0.5, 0.5)

        rotation = Rotation(0.0, 1.0, 0.0, 0.0)
        origin = V3(0.5, 0.5, 0.5)

        scale = V3(8.0, 8.0, 8.0)

        afterRender = {
            RenderHelper.disableStandardItemLighting()
        }

        color = WHITE
        size = V3(14.0, 10.0, 14.0)
        textureLocation =
            clientApi.resourceManager().getLocation("minecraft", "textures/entity/chest/normal.png")
        textureSize = V3(64.0, 64.0)
        textureFrom = V2(0.0, 19.0)
        addChild(chestLid, rotationIntensity)
    }

    private val wrapper = rectangle {
        addChild(chest)
        align = CENTER
        rotation = Rotation(0.0, 0.0, 1.0, 0.0)

        beforeRender = {
            GlStateManager.disableCull()
            GlStateManager.enableDepth()
            RenderHelper.enableGUIStandardItemLighting()
            GlStateManager.enableRescaleNormal()
        }

        afterRender = {
            RenderHelper.disableStandardItemLighting()
        }
    }

    val body1 = rectangle {
        rotation = Rotation(y = 1.0, z = 0.0)
        addChild(wrapper)
        addChild(glowRect)
        addChild(itemRect)
    }

    val body2 = rectangle {
        rotation = Rotation(x = 1.0, z = 0.0)
        align = CENTER
        addChild(body1)
    }

    private val perspective = rectangle {
        enabled = false
        beforeRender = {
            GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT)
            GlStateManager.matrixMode(GL11.GL_PROJECTION)
            GlStateManager.pushMatrix()
            GlStateManager.loadIdentity()
            val screen = UIEngine.overlayContext.size
            GlStateManager.translate(offset.x / screen.x * 2, 0.0, 0.0)
            GLU.gluPerspective(90f, 1920f / 1080f, 0.01f, 500f)
            GlStateManager.scale(1920 / screen.x / 2.6, 1080 / screen.y / 2.6, 1.0)
            GlStateManager.matrixMode(GL11.GL_MODELVIEW)
            GlStateManager.loadIdentity()
            GlStateManager.translate(0f, 0f, -300f)
            GlStateManager.scale(1f, -1f, 1f)
            GlStateManager.disableCull()
        }
        afterRender = {
            GlStateManager.matrixMode(GL11.GL_PROJECTION)
            GlStateManager.popMatrix()
            GlStateManager.matrixMode(GL11.GL_MODELVIEW)
        }
        align = CENTER

        addChild(body2)

        UIEngine.overlayContext.addChild(background, text, this, vignette, rarityText, nameText)
    }

    private lateinit var loot: List<Loot>
    var lootIndex = 0

    fun setup(loot: List<Loot>) {
        this.loot = loot
        lootIndex = 0
    }

    private fun setupNextItem() {
        if (hasNextItem()) {
            setupLoot(loot[lootIndex])
            lootIndex++
        }
    }

    fun hasNextItem() = lootIndex < loot.size

    private fun setupLoot(loot: Loot) {
        val color = loot.rarity.color

        this.rarityText.color = color
        this.vignette.color = color
        this.vignette.color.alpha = 0.0
        this.itemRect.color = color
        this.itemRect.color.alpha = 0.28
        this.itemRect.children[0].color = color
        this.glowRect.color = color
        this.glowRect.color.alpha = 0.85
        this.rarityText.content = loot.rarity.name
        (this.itemRect.children[1] as ItemElement).stack = loot.item
        this.nameText.content = loot.name
    }

    fun prepareToOpen() {
        background.enabled = true
        text.content = "Нажмите [ЛКМ], чтобы открыть лутбокс"
        text.color.alpha = 1.0
        rotationIntensity.color.alpha = 0.75
        perspective.enabled = true
        wrapper.align.y = -0.5
        chest.scale = V3(3.0, 3.0, 3.0)
        chest.animate(0.8) { chest.scale = V3(8.0, 8.0, 8.0) }

        wrapper.rotation.degrees = -1.0
        wrapper.animate(0.8) { wrapper.rotation.degrees = Math.PI + 0.4 }

        UIEngine.schedule(0.8) {
            wrapper.animate(0.4, Easings.BACK_OUT) { wrapper.rotation.degrees = Math.PI }
        }
        itemRect.enabled = false

        wrapper.animate(1.1, Easings.BACK_OUT) { wrapper.align.y = 0.5 }

        chestLid.rotation.degrees = 0.0
        chest.rotation.degrees = 0.0
        chest.offset.y = 0.0

        glowRect.scale = V3()
        glowRect.color.alpha = 0.0
        nameText.color.alpha = 0.0
        rarityText.color.alpha = 0.0
    }

    fun close() {
        background.enabled = false
        text.color.alpha = 0.0
        perspective.enabled = false
        itemRect.enabled = false
        nameText.color.alpha = 0.0
        rarityText.color.alpha = 0.0
    }

    fun shake() {
        chest.animate(0.05) {
            chest.offset.x = (Math.random() - 0.5) * 64.0
            chest.offset.y = (Math.random() - 0.5) * 64.0
            chest.offset.z = (Math.random() - 0.5) * 64.0
        }
    }

    fun open() {
        setupNextItem()
        text.color.alpha = 0.0
        itemRect.enabled = true
        itemRect.offset.y = 0.0
        clientApi.clientConnection().sendPayload("lootbox:opened", Unpooled.EMPTY_BUFFER)

        itemRect.animate(0.3, Easings.BACK_OUT) { offset.y = -30.0 }
        glowRect.animate(0.2) {
            scale = V3(1.0, 1.0, 1.0)
            color.alpha = 0.8
        }
        vignette.animate(0.2) { vignette.color.alpha = 0.8 }

        UIEngine.schedule(0.5) {
            vignette.animate(0.8, Easings.CUBIC_OUT) { color.alpha = 0.0 }
        }

        nameText.animate(0.5) { color.alpha = 1.0 }
        rarityText.animate(0.5) { color.alpha = 1.0 }
        chestLid.animate(0.6, Easings.QUINT_OUT) { rotation.degrees = -Math.PI * 0.57 }

        chest.animate(0.6, Easings.QUINT_OUT) {
            chest.offset = V3()
            chest.offset.y = 40.0
            rotation.degrees = 0.35
        }

        rotationIntensity.animate(0.6, Easings.QUINT_OUT) { color.alpha = 0.15 }
        chest.animate(0.6, Easings.BACK_OUT) { chest.scale = V3(8.0, 8.0, 8.0) }

        val hasNext = hasNextItem()

        text.content = if (hasNext) "Нажмите [ЛКМ], чтобы продолжить" else "Нажмите [ЛКМ], чтобы закрыть меню"
        text.animate(0.5) { color.alpha = 1.0 }

        if (!hasNext) {
            UIEngine.schedule(0.5) {
                opened = true
            }
        }
    }
}