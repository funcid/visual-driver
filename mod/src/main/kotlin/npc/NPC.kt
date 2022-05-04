package npc

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.inventory.EntityEquipmentSlot
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.clientapi.math.BlockPos
import dev.xdark.clientapi.util.EnumFacing
import dev.xdark.clientapi.util.EnumHand
import dev.xdark.feder.NetUtil
import me.func.protocol.npc.NpcBehaviour
import me.func.protocol.npc.NpcData
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import java.util.UUID
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.sqrt

class NPC : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        // Утилита для работы с NPC
        NpcManager

        // Чтение NPC
        registerChannel("npc:spawn") {
            val data = NpcData(
                readInt(),
                UUID.fromString(NetUtil.readUtf8(this)),
                readDouble(),
                readDouble(),
                readDouble(),
                readInt(),
                NetUtil.readUtf8(this),
                NpcBehaviour.values()[readInt()],
                readDouble().toFloat(),
                readDouble().toFloat(),
                NetUtil.readUtf8(this),
                NetUtil.readUtf8(this),
                readBoolean(),
                readBoolean(),
                readBoolean(),
                readBoolean()
            )
            NpcManager.spawn(data)
            NpcManager.show(data.uuid)
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

        // Обновить метаданные NPC
        registerChannel("npc:update") {
            UUID.fromString(NetUtil.readUtf8(this)).apply {
                NpcManager.get(this)?.let { entity ->
                    entity.entity?.let { npc ->
                        npc.customNameTag = NetUtil.readUtf8(this@registerChannel)

                        npc.teleport(readDouble(), readDouble(), readDouble())
                        npc.setYaw(readDouble().toFloat())
                        npc.setPitch(readDouble().toFloat())

                        if (readBoolean()) npc.enableRidingAnimation()
                        else npc.disableRidingAnimation()
                        if (readBoolean()) npc.enableSleepAnimation(
                            BlockPos.of(
                                npc.x.toInt(),
                                npc.y.toInt(),
                                npc.z.toInt()
                            ), EnumFacing.DOWN
                        )
                        else npc.disableSleepAnimation()
                        npc.isSneaking = readBoolean()
                    }
                }
            }
        }

        // Ударить рукой
        registerChannel("npc:kick") {
            UUID.fromString(NetUtil.readUtf8(this)).apply {
                NpcManager.get(this)?.let { entity ->
                    entity.entity?.swingArm(if (readBoolean()) EnumHand.MAIN_HAND else EnumHand.OFF_HAND)
                }
            }
        }

        // Удалить всех NPC
        registerChannel("npc:kill-all") {
            NpcManager.each { uuid, _ ->
                NpcManager.hide(uuid)
                NpcManager.kill(uuid)
            }
        }

        // Изменение предмета в инвентаре
        registerChannel("npc:slot") {
            NpcManager.get(UUID.fromString(NetUtil.readUtf8(this)))?.let {
                it.entity?.let { npc ->
                    readInt().let { slot ->
                        val item = ItemTools.read(this)

                        when (slot) {
                            0 -> npc.setItemInSlot(EntityEquipmentSlot.MAINHAND, item)
                            1 -> npc.setItemInSlot(EntityEquipmentSlot.OFFHAND, item)
                            2 -> npc.setItemInSlot(EntityEquipmentSlot.FEET, item)
                            3 -> npc.setItemInSlot(EntityEquipmentSlot.LEGS, item)
                            4 -> npc.setItemInSlot(EntityEquipmentSlot.CHEST, item)
                            5 -> npc.setItemInSlot(EntityEquipmentSlot.HEAD, item)
                        }
                    }
                }
            }
        }

        registerHandler<GameLoop> {
            val player = clientApi.minecraft().player

            NpcManager.each { _, data ->
                data.entity?.let { entity ->
                    if (data.data.behaviour == NpcBehaviour.NONE)
                        return@let
                    val dx: Double = player.x - entity.x
                    var dy: Double = player.y - entity.y
                    val dz: Double = player.z - entity.z

                    val active = dx * dx + dy * dy + dz * dz < 196

                    dy /= sqrt(dx * dx + dz * dz)
                    val yaw = if (active) (atan2(-dx, dz) / Math.PI * 180).toFloat() else data.data.yaw

                    entity.apply {
                        rotationYawHead = yaw
                        setYaw(yaw)
                        setPitch((atan(-dy) / Math.PI * 180).toFloat())
                    }
                }
            }
        }
    }
}