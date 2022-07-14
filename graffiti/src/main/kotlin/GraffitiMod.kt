import dev.xdark.clientapi.event.entity.RotateAround
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.input.MousePress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.*
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import me.func.protocol.DropRare
import me.func.protocol.personalization.*
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.onMouseUp
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin

lateinit var mod: GraffitiMod
const val PICTURE_SIZE = 2049
const val OVAL_RADIUS = 100
const val ICON_PACK_SIZE = 32.0
const val BASE_SCALE = 0.22

class GraffitiMod : KotlinMod() {

    val texture: ResourceLocation = ResourceLocation.of("cache/animation", "graffiti.png")

    val gui = ContextGui()
    var open = false
    private var inited = false

    lateinit var userData: FeatureUserData
    lateinit var packs: MutableList<LocalPack>

    var activeGraffiti: LocalGraffitiPlaced? = null
    private var drewGraffities = mutableListOf<LocalGraffitiPlaced>()

    fun moveCursor(index: Int) {
        mod.gui.children.clear()
        mod.userData.activePack = index
        mod.loadPackIntoMenu()
    }

    fun getPack(uuid: UUID): GraffitiPack {
        // Получить пак по UUID
        return userData.packs.first { it.uuid == uuid }
    }

    private fun getActivePack(): LocalPack {
        // Получить выбранный пак игрока
        return packs[userData.activePack]
    }

    private fun setRotation(graffiti: LocalGraffitiPlaced, rotation: Rotation) {
        // Установить поворот граффити
        graffiti.graffiti.rotationAngle = rotation.degrees
        graffiti.graffiti.rotationAxisX = rotation.x
        graffiti.graffiti.rotationAxisY = rotation.y
        graffiti.graffiti.rotationAxisZ = rotation.z
        graffiti.container.rotation = rotation
    }

    private fun addGraffiti(graffiti: LocalGraffitiPlaced) {
        // Если этот игрок поставил граффити - убрать использование
        if (graffiti.graffiti.owner == clientApi.minecraft().player.uniqueID) {
            userData.packs.forEach { pack ->
                pack.graffiti.find { it.uuid == graffiti.graffiti.graffiti.uuid }?.let { it.uses-- }
            }
        }

        val mark = graffiti.graffiti

        // Добавить граффити в мир
        val context = graffiti.container

        context.offset.x = graffiti.graffiti.x
        context.offset.y = graffiti.graffiti.y
        context.offset.z = graffiti.graffiti.z

        context.beforeRender {
            if (mark.onGround) {
                val matrix = Matrix4f()
                Matrix4f.setIdentity(matrix)
                Matrix4f.rotate(mark.extraRotation.toFloat(), Vector3f(0f, -1f, 0f), matrix, matrix)
                Matrix4f.rotate((-Math.PI / 2).toFloat(), Vector3f(1f, 0f, 0f), matrix, matrix)
                context.matrices[rotationMatrix] = matrix
            }
        }

        val realGraffiti = rectangle {
            origin = CENTER
            align = CENTER
            color = WHITE

            textureLocation = mod.texture
            textureFrom =
                V3(mark.graffiti.address.x.toDouble() / PICTURE_SIZE, mark.graffiti.address.y.toDouble() / PICTURE_SIZE)
            textureSize = V3(
                mark.graffiti.address.size.toDouble() / PICTURE_SIZE,
                mark.graffiti.address.size.toDouble() / PICTURE_SIZE
            )

            size = V3(20.0, 20.0)
            offset = V3(10.0, 10.0)
        }

        setRotation(graffiti, Rotation(mark.rotationAngle, mark.rotationAxisX, mark.rotationAxisY, mark.rotationAxisZ))

        context.addChild(realGraffiti)
        context.addChild(graffiti.indicatorContainer)
        context.addChild(graffiti.author)
        context.addChild(graffiti.authorShadow)

        graffiti.indicator.animate(mark.ticksLeft / 19.0) { size.x = 0.0 }

        UIEngine.worldContexts.add(context)

        // Удалить ихз мира через время
        UIEngine.schedule(mark.ticksLeft / 19.0) {
            context.removeChild(realGraffiti)
            UIEngine.worldContexts.remove(context)
            drewGraffities.remove(graffiti)
        }
    }

    private fun readLocalGraffitiPlace(buffer: ByteBuf): LocalGraffitiPlaced {
        val graffiti = GraffitiPlaced(
            UUID.fromString(NetUtil.readUtf8(buffer)),
            NetUtil.readUtf8(buffer),
            NetUtil.readUtf8(buffer),
            Graffiti(
                GraffitiInfo(
                    UUID.fromString(NetUtil.readUtf8(buffer)),
                    buffer.readInt(),
                    buffer.readInt(),
                    buffer.readInt(),
                    buffer.readInt()
                ), NetUtil.readUtf8(buffer), buffer.readInt()
            ),
            buffer.readDouble(),
            buffer.readDouble(),
            buffer.readDouble(),
            buffer.readInt(),
            buffer.readDouble(),
            buffer.readDouble(),
            buffer.readDouble(),
            buffer.readDouble(),
            buffer.readDouble(),
            buffer.readBoolean(),
        )

        return LocalGraffitiPlaced(graffiti, Context3D(V3(graffiti.x, graffiti.y, graffiti.z)))
    }

    private fun sendGraffitiToServer(localGraffiti: LocalGraffitiPlaced) {
        // Отправить граффити на сервер
        val graffiti = localGraffiti.graffiti
        val buffer = Unpooled.buffer()

        NetUtil.writeUtf8(getActivePack().packUuid.toString(), buffer)
        NetUtil.writeUtf8(graffiti.graffiti.uuid.toString(), buffer)
        buffer.writeDouble(graffiti.x)
        buffer.writeDouble(graffiti.y)
        buffer.writeDouble(graffiti.z)
        buffer.writeDouble(graffiti.rotationAngle)
        buffer.writeDouble(graffiti.rotationAxisX)
        buffer.writeDouble(graffiti.rotationAxisY)
        buffer.writeDouble(graffiti.rotationAxisZ)
        buffer.writeDouble(graffiti.extraRotation)
        buffer.writeBoolean(graffiti.onGround)

        clientApi.clientConnection().sendPayload("graffiti:use", buffer)
    }

    private fun buyPack(pack: LocalPack) {
        // Купить пак на сервере
        val buffer = Unpooled.buffer()

        NetUtil.writeUtf8(buffer, pack.packUuid.toString())

        clientApi.clientConnection().sendPayload("graffiti:buy", buffer)
    }

    fun loadPackIntoMenu() {
        val active = getActivePack()
        val pack = getPack(active.packUuid)
        val rare = DropRare.values()[pack.rare]

        GlowEffect.showAlways(rare.red, rare.green, rare.blue, 0.2)

        active.graffiti.forEachIndexed { index, element ->
            element.icon.scale.x = BASE_SCALE
            element.icon.scale.y = BASE_SCALE

            val angle = 2 * Math.PI / active.graffiti.size * index
            element.icon.offset.x = sin(angle) * OVAL_RADIUS
            element.icon.offset.y = cos(angle) * OVAL_RADIUS * 0.85 - 30
            element.icon.enabled = true

            gui + element.icon
        }
        gui + active.title
        /*val text = "Купить - ${pack.price} кристаликов"
        gui + carved {
            align = CENTER
            origin = CENTER
            color = Color(42, 102, 189, 1.0)
            size = V3(UIEngine.clientApi.fontRenderer().getStringWidth(text) + 12.0, 17.0)
            offset.y += OVAL_RADIUS + 20
            +text {
                align = CENTER
                origin = CENTER
                color = WHITE
                shadow = true
                content = text
            }
            onMouseUp { if (button == MouseButton.LEFT) buyPack(active) }
        }*/

        gui + flex {
            flexSpacing = 5.0

            align = CENTER
            origin = CENTER

            offset.y += OVAL_RADIUS + 30

            packs.forEachIndexed { index, it ->
                +it.iconContainer.apply {
                    val boost = if (index == mod.userData.activePack) 6 else 0

                    it.iconContainer.size.x = ICON_PACK_SIZE + boost
                    it.iconContainer.size.y = ICON_PACK_SIZE + boost
                    it.icon.size.x = ICON_PACK_SIZE + boost - 4
                    it.icon.size.y = ICON_PACK_SIZE + boost - 4

                    onMouseUp { moveCursor(index) }
                }
            }
        }
    }

    override fun onEnable() {
        mod = this
        UIEngine.initialize(this)

        fun safeOpen() = open && !clientApi.minecraft().inGameHasFocus()

        registerHandler<HealthRender> { if (safeOpen()) isCancelled = true }
        registerHandler<HungerRender> { if (safeOpen()) isCancelled = true }
        registerHandler<ArmorRender> { if (safeOpen()) isCancelled = true }
        registerHandler<ExpBarRender> { if (safeOpen()) isCancelled = true }
        registerHandler<HandRender> { if (safeOpen()) isCancelled = true }
        registerHandler<HotbarRender> { if (safeOpen()) isCancelled = true }

        gui.onKeyTyped { char, code ->
            if (code == Keyboard.KEY_LEFT || code == Keyboard.KEY_DOWN) moveCursor(maxOf(0, userData.activePack - 1))
            else if (code == Keyboard.KEY_RIGHT || code == Keyboard.KEY_UP) moveCursor(
                minOf(
                    packs.size - 1,
                    userData.activePack + 1
                )
            ) else if (code == Keyboard.KEY_ESCAPE) open = false
        }

        val prompt = text {
            align = CENTER
            origin = CENTER
            shadow = true
            content = "§bРазместить граффити §lПКМ\n§cУбрать §lЛКМ"
            offset.y += 30
        }

        UIEngine.overlayContext + prompt

        registerHandler<GameLoop> {
            prompt.enabled = activeGraffiti != null

            if (open) {
                if (activeGraffiti != null) open = false
                val move = Mouse.getDWheel()
                if (move == 0) return@registerHandler

                moveCursor(maxOf(0, minOf(userData.activePack - sign(move.toDouble()).toInt(), packs.size - 1)))
            } else {
                val player = clientApi.minecraft().player
                drewGraffities.forEach {
                    val v3 = it.container.offset
                    val close =
                        (v3.x - player.x).pow(2.0) + (v3.y - player.y).pow(2.0) + (v3.z - player.z).pow(2.0) < 10
                    it.authorShadow.enabled = close
                    it.author.enabled = close
                    it.indicatorContainer.enabled = close
                }
            }
        }

        gui.color = Color(0, 0, 0, 0.86)

        // Загрузить кучу граффити (Например когда игрок меняет мир)
        registerChannel("graffiti:create-bulk") {
            // Удалить все граффити из мира
            drewGraffities.forEach { UIEngine.worldContexts.remove(it.container) }

            // Поставить в мире новые граффити
            drewGraffities = MutableList(readInt()) { readLocalGraffitiPlace(this) }
            drewGraffities.forEach { addGraffiti(it) }

            // Начать показывать подсказки
            startShowHints()
        }

        // Загрузить единичное граффити
        registerChannel("graffiti:create") {
            // Добавить граффити в мир
            val graffiti = readLocalGraffitiPlace(this)
            drewGraffities.add(graffiti)
            addGraffiti(graffiti)
        }

        // Получение данных о граффити игрока
        registerChannel("graffiti:init") {
            // Загрузка данных
            userData = FeatureUserData(
                UUID.fromString(NetUtil.readUtf8(this)), // user uuid
                MutableList(readInt()) { // packs amount
                    GraffitiPack(
                        UUID.fromString(NetUtil.readUtf8(this)), // pack uuid
                        MutableList(readInt()) { // graffiti amount
                            Graffiti(
                                GraffitiInfo(
                                    UUID.fromString(NetUtil.readUtf8(this)), // uuid
                                    readInt(), // x
                                    readInt(), // y
                                    readInt(), // size
                                    readInt() // maxUses
                                ),
                                NetUtil.readUtf8(this), // author
                                readInt(), // uses
                            )
                        }, NetUtil.readUtf8(this), // title
                        NetUtil.readUtf8(this), // creator
                        readInt(), // price
                        readInt(), // rare
                        readBoolean() // available
                    )
                }, readInt(), // active pack,
                mutableListOf(), UUID.randomUUID()
                //MutableList(readInt()) { // count
                //    Sticker(
                //        UUID.fromString(NetUtil.readUtf8(this)), // uuid
                //        NetUtil.readUtf8(this), // name
                //        DropRare.values()[readInt()], // rare
                //        readLong() // openTime
                //        )
                //}, UUID.randomUUID()
                //if (isReadable)  // present
                //    UUID.fromString(NetUtil.readUtf8(this))
                //else null
            )
            packs = userData.packs.mapIndexed { index, pack -> LocalPack(pack.uuid, index) }.toMutableList()

            inited = true
            println("Graffiti mod successfully loaded!")

            // Включить возможность выбора места под граффити
            startPickPlace()
        }

        // Если игрок купил пак
        registerChannel("graffiti:bought") {
            // Заполнить купленный пак граффити
            getPack(UUID.fromString(NetUtil.readUtf8(this))).let { pack ->
                pack.graffiti.forEach { it.uses += it.address.maxUses }
            }
        }


        fun pick() {
            if (activeGraffiti != null) {
                // Вернуть граффити в меню
                getActivePack().backGraffitiToPack(activeGraffiti!!)

                // Поставить граффити у всех игроков
                sendGraffitiToServer(activeGraffiti!!)

                // Очистить выбранное граффити
                activeGraffiti = null
                open = false
            }
        }

        registerHandler<MousePress> {
            if (activeGraffiti != null) {
                if (button == MouseButton.LEFT.ordinal) pick()
                else if (button == MouseButton.RIGHT.ordinal) activeGraffiti = null
            }
        }

        registerHandler<KeyPress> {
            if (!inited) return@registerHandler

            // Выбрать другое граффити
            if (key == Keyboard.KEY_H) {
                open = if (activeGraffiti == null) {
                    loadPackIntoMenu()
                    gui.open()
                    true
                } else {
                    gui.children.clear()
                    gui.close()
                    false
                }
            } else if (key == Keyboard.KEY_NEXT) pick()
        }
    }

    private fun startPickPlace() {
        registerHandler<RotateAround> {
            if (activeGraffiti == null) return@registerHandler

            val player = clientApi.minecraft().player
            val viewDistance = 4.5

            val graffiti = activeGraffiti!!.graffiti
            val look = entity.lookVec

            val x = entity.x
            val y = entity.y + 1.5
            val z = entity.z

            val world = clientApi.minecraft().world

            val yaw = (360 * 10000 + player.rotationYaw) % 360

            for (i in 1..((viewDistance * 100).toInt())) {
                val dx = look.x / 100 * i
                val dy = look.y / 100 * i
                val dz = look.z / 100 * i
                val newX = x + dx
                val newY = y + dy
                val newZ = z + dz

                val id = world.getBlockState(newX, newY, newZ).block.id
                if (id != 0 && id != 6 && id != 31 && id != 37 && id != 38 && id != 68 && id != 107 && id != 131 && id != 132 && id != 143 && id != 160) {

                    var moveX = newX - dx / 60
                    var moveY = newY
                    var moveZ = newZ - dz / 60

                    val onGround = player.rotationPitch >= 90 / viewDistance
                    graffiti.onGround = onGround

                    if (onGround) {
                        moveY -= 0.47

                        val matrix = Matrix4f()
                        Matrix4f.setIdentity(matrix)

                        val playerRotation = (player.rotationYaw + 180) / 180 * Math.PI

                        Matrix4f.rotate(
                            playerRotation.toFloat(),
                            Vector3f(0f, -1f, 0f),
                            matrix,
                            matrix
                        )
                        Matrix4f.rotate((-Math.PI / 2).toFloat(), Vector3f(1f, 0f, 0f), matrix, matrix)

                        activeGraffiti!!.container.matrices[rotationMatrix] = matrix
                        activeGraffiti!!.graffiti.extraRotation = playerRotation
                        setRotation(activeGraffiti!!, Rotation(-Math.PI / 2, 1.0, 0.0, 0.0))
                    } else {
                        moveY += 0.3
                    }

                    when (yaw) {
                        in 45.0..135.0 -> {
                            moveY += 0.5
                            moveZ += 0.5
                            if (!onGround) {
                                setRotation(activeGraffiti!!, Rotation(Math.PI / 2, 0.0, 1.0, 0.0))
                            }
                        }
                        in 225.0..315.0 -> {
                            moveY += 0.5
                            moveZ -= 0.5
                            if (!onGround) setRotation(activeGraffiti!!, Rotation(-Math.PI / 2, 0.0, 1.0, 0.0))
                        }
                        !in 135.0..225.0 -> {
                            moveY += 0.5
                            moveX += 0.5
                            if (!onGround) setRotation(activeGraffiti!!, Rotation(-Math.PI, 0.0, 1.0, 0.0))
                        }
                        else -> {
                            moveY += 0.5
                            moveX -= 0.5
                            if (!onGround) setRotation(activeGraffiti!!, Rotation(Math.PI * 2, 0.0, 1.0, 0.0))
                        }
                    }

                    activeGraffiti!!.container.animate(0.03) {
                        offset.x = moveX
                        offset.y = moveY
                        offset.z = moveZ
                    }

                    graffiti.x = moveX
                    graffiti.y = moveY
                    graffiti.z = moveZ
                    break
                }
            }
        }
    }

    private fun startShowHints() {
        val player = clientApi.minecraft().player

        registerHandler<RenderTickPre> {
            //val nearGraffiti = drewGraffities.firstOrNull {
            //    val location = it.first.offset
            //    pow(location.x - player.x, 2.0) + pow(location.z - player.z, 2.0) <= 1.2 &&
            //            abs(location.y - player.y) < 2.6
            //}
        }
    }
}
