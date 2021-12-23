import dev.xdark.clientapi.event.chunk.ChunkLoad
import dev.xdark.clientapi.event.chunk.ChunkUnload
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.util.EnumHand
import dev.xdark.clientapi.world.World
import dev.xdark.clientapi.world.chunk.Chunk
import dev.xdark.feder.NetUtil
import me.func.protocol.npc.NpcBehaviour
import me.func.protocol.npc.NpcData
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import java.util.*
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.sqrt


class App : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        // Утилита для работы с NPC
        NpcManager

        // Чтение NPC
        registerChannel("npc:spawn") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))

            NpcManager.spawn(
                NpcData(
                    uuid,
                    readInt(),
                    readInt(),
                    NetUtil.readUtf8(this),
                    NpcBehaviour.values()[readInt()],
                    readDouble(),
                    readDouble(),
                    readDouble(),
                    readFloat(),
                    readFloat(),
                    NetUtil.readUtf8(this),
                    NetUtil.readUtf8(this),
                    readBoolean()
                )
            )

            NpcManager.get(uuid)?.let {
                val world: World = clientApi.minecraft().world

                // Если чанк прогружен - показать NPC
                world.chunkProvider.getLoadedChunk(it.data.x.toInt() shr 4, it.data.z.toInt() shr 4)?.let {
                    NpcManager.show(uuid)
                }
            }
        }

        // Скрыть NPC
        registerChannel("npc:hide") {
            NpcManager.hide(UUID.fromString(NetUtil.readUtf8(this)))
        }

        // Показать NPC
        registerChannel("npc:show") {
            NpcManager.show(UUID.fromString(NetUtil.readUtf8(this)))
        }

        // Удалить NPC
        registerChannel("npc:kill") {
            UUID.fromString(NetUtil.readUtf8(this)).apply {
                NpcManager.hide(this)
                NpcManager.kill(this)
            }
        }

        // Удалить всех NPC
        registerChannel("npc:kill") {
            NpcManager.each { uuid, _ ->
                NpcManager.hide(uuid)
                NpcManager.kill(uuid)
            }
        }

        // Проверка на нахождение в чанке
        fun inside(chunk: Chunk, x: Double, z: Double) = x.toInt() shr 4 == chunk.x && z.toInt() shr 4 == chunk.z

        // При загрузке чанка показывать NPC
        registerHandler<ChunkLoad> {
            NpcManager.each { uuid, data -> data.entity?.let { if (inside(chunk, it.x, it.z)) NpcManager.show(uuid) } }
        }

        // При отгрузке чанка скрывать NPC
        registerHandler<ChunkUnload> {
            NpcManager.each { uuid, data -> data.entity?.let { if (inside(chunk, it.x, it.z)) NpcManager.hide(uuid) } }
        }

        // Постоянный цикл
        var ticks = 0

        registerHandler<GameLoop> {
            val tick = ticks++ % 600
            val player = clientApi.minecraft().player

            NpcManager.each { _, data ->
                data.entity?.let { entity ->
                    if (data.data.behaviour == NpcBehaviour.NONE)
                        return@let
                    val lookAround = data.data.behaviour == NpcBehaviour.STARE_AND_LOOK_AROUND

                    if (lookAround && (tick == 500 || tick == 510))
                        entity.swingArm(EnumHand.MAIN_HAND)

                    val dYaw = if (tick in 41..69) -40f else if (tick in 76..129) +40f else 0f

                    val resetPitch = tick in 41..129

                    val dx: Double = player.x - entity.x
                    var dy: Double = player.y - entity.y
                    val dz: Double = player.z - entity.z

                    val active = dx * dx + dy * dy + dz * dz < 36

                    dy /= sqrt(dx * dx + dz * dz)
                    var yaw =
                        if (active) (atan2(-dx, dz) / Math.PI * 180).toFloat() else data.data.yaw
                    if (lookAround)
                        yaw += dYaw

                    entity.apply {
                        rotationYawHead = yaw
                        setYaw(yaw)
                        setPitch(if (!active || resetPitch && lookAround)0f else (atan(-dy) / Math.PI * 180).toFloat())
                    }
                }
            }
        }
    }
}