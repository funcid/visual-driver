import dev.xdark.clientapi.event.entity.RotateAround
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.render.ArmorRender
import dev.xdark.clientapi.event.render.ExpBarRender
import dev.xdark.clientapi.event.render.HealthRender
import dev.xdark.clientapi.event.render.HungerRender
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import me.func.protocol.DropRare
import me.func.protocol.personalization.FeatureUserData
import me.func.protocol.personalization.Graffiti
import me.func.protocol.personalization.GraffitiInfo
import me.func.protocol.personalization.GraffitiPack
import me.func.protocol.personalization.GraffitiPlaced
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.Rotation
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.WHITE
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.rotationMatrix
import ru.cristalix.uiengine.utility.text
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

lateinit var graffitiMod: GraffitiMod
const val PICTURE_SIZE = 1920
const val OVAL_RADIUS = 90
const val ICON_PACK_SIZE = 20.0

class GraffitiMod : KotlinMod() {

    val texture: ResourceLocation = ResourceLocation.of("cache/animation", "graffiti.png")

    val gui = ContextGui()
    var open = false
    private var inited = false

    lateinit var userData: FeatureUserData
    lateinit var packs: MutableList<LocalPack>

    var activeGraffiti: LocalGraffitiPlaced? = null
    private var drewGraffities = mutableListOf<LocalGraffitiPlaced>()

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
        graffiti.context3D.rotation = rotation
    }

    private fun addGraffiti(graffiti: LocalGraffitiPlaced) {
        // Добавить граффити в мир
        val context = graffiti.context3D

        context.offset.x = graffiti.graffiti.x
        context.offset.y = graffiti.graffiti.y
        context.offset.z = graffiti.graffiti.z

        val mark = graffiti.graffiti
        val realGraffiti = rectangle {
            origin = CENTER
            align = CENTER
            color = WHITE

            textureLocation = graffitiMod.texture
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
            buffer.readBoolean(),
            buffer.readBoolean()
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
        buffer.writeBoolean(graffiti.onGround)
        buffer.writeBoolean(false)

        graffiti.graffiti.uses--

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

        GlowEffect.showAlways(rare.red, rare.green, rare.blue, 0.11)

        active.graffiti.forEachIndexed { index, element ->
            element.icon.scale.x = 0.25
            element.icon.scale.y = 0.25

            val angle = 2 * Math.PI / active.graffiti.size * index
            element.icon.offset.x = sin(angle) * OVAL_RADIUS
            element.icon.offset.y = cos(angle) * OVAL_RADIUS * 0.75 - 40
            element.icon.enabled = true

            gui + element.icon
        }
        gui + active.title
        val text = "Купить - ${pack.price} кристаликов"
        gui + rectangle {
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
            onClick { if (Mouse.isButtonDown(0)) buyPack(active) }
        }
        packs.forEachIndexed { index, it ->
            gui + it.icon.apply {
                val boost = if (index == graffitiMod.userData.activePack) 2 else 0
                it.icon.size.x = ICON_PACK_SIZE + boost
                it.icon.size.y = ICON_PACK_SIZE + boost
            }
        }
    }

    override fun onEnable() {
        graffitiMod = this
        UIEngine.initialize(this)

        registerHandler<HealthRender> { if (open) isCancelled = true }
        registerHandler<HungerRender> { if (open) isCancelled = true }
        registerHandler<ArmorRender> { if (open) isCancelled = true }
        registerHandler<ExpBarRender> { if (open) isCancelled = true }

        gui.color = Color(0, 0, 0, 0.86)

        // Загрузить кучу граффити (Например когда игрок меняет мир)
        registerChannel("graffiti:create-bulk") {
            // Удалить все граффити из мира
            drewGraffities.forEach { UIEngine.worldContexts.remove(it.context3D) }

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
            }

            if (key == Keyboard.KEY_J) {
                if (activeGraffiti != null && !open) {
                    // Вернуть граффити в меню
                    getActivePack().backGraffitiToPack(activeGraffiti!!)

                    // Поставить граффити у всех игроков
                    sendGraffitiToServer(activeGraffiti!!)

                    // Очистить выбранное граффити
                    activeGraffiti = null
                }
            }
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
                        Matrix4f.rotate(
                            ((player.rotationYaw + 180) / 180 * Math.PI).toFloat(),
                            Vector3f(0f, -1f, 0f),
                            matrix,
                            matrix
                        )
                        Matrix4f.rotate((-Math.PI / 2).toFloat(), Vector3f(1f, 0f, 0f), matrix, matrix)
                        activeGraffiti!!.context3D.matrices[rotationMatrix] = matrix

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

                    activeGraffiti!!.context3D.animate(0.03) {
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
