package npc

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.inventory.EntityEquipmentSlot
import dev.xdark.clientapi.item.ItemTools
import dev.xdark.clientapi.math.BlockPos
import dev.xdark.clientapi.util.EnumFacing
import dev.xdark.clientapi.util.EnumHand
import dev.xdark.feder.NetUtil
import me.func.protocol.world.npc.NpcBehaviour
import me.func.protocol.world.npc.NpcData
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import java.util.*
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class NPC {
    init {
        // Утилита для работы с NPC
        val npcManager = NpcManager()

        // Чтение NPC
        mod.registerChannel("npc:spawn") {
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
                NetUtil.readUtf8(this).run {
                    if (this == "self") {
                        return@run "https://webdata.c7x.dev/textures/skin/${UIEngine.clientApi.minecraft().player.uniqueID}"
                    }
                    return@run this
                },
                NetUtil.readUtf8(this),
                NetUtil.readUtf8(this),
                NetUtil.readUtf8(this),
                readBoolean(),
                readBoolean(),
                readBoolean(),
                readBoolean()
            )
            npcManager.spawn(data)
            npcManager.show(data.uuid)
        }

        // Скрыть NPC
        mod.registerChannel("npc:hide") {
            npcManager.hide(UUID.fromString(NetUtil.readUtf8(this)))
        }

        // Показать NPC
        mod.registerChannel("npc:show") {
            npcManager.show(UUID.fromString(NetUtil.readUtf8(this)))
        }

        // Удалить NPC
        mod.registerChannel("npc:kill") {
            UUID.fromString(NetUtil.readUtf8(this)).apply {
                npcManager.hide(this)
                npcManager.kill(this)
            }
        }

        // Обновить метаданные NPC
        mod.registerChannel("npc:update") {
            UUID.fromString(NetUtil.readUtf8(this)).apply {
                npcManager.get(this)?.let { entity ->
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
        mod.registerChannel("npc:kick") {
            UUID.fromString(NetUtil.readUtf8(this)).apply {
                npcManager.get(this)?.let { entity ->
                    entity.entity?.swingArm(if (readBoolean()) EnumHand.MAIN_HAND else EnumHand.OFF_HAND)
                }
            }
        }

        // Удалить всех NPC
        mod.registerChannel("npc:kill-all") {
            npcManager.each { uuid, _ ->
                npcManager.hide(uuid)
                npcManager.kill(uuid)
            }
        }

        // Изменение предмета в инвентаре
        mod.registerChannel("npc:slot") {
            npcManager.get(UUID.fromString(NetUtil.readUtf8(this)))?.let {
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

        mod.registerHandler<GameLoop> {
            val player = clientApi.minecraft().player

            npcManager.each { _, data ->
                data.entity?.let { entity ->
                    if (data.data.behaviour == NpcBehaviour.NONE)
                        return@let
                    val dx: Double = player.x - entity.x
                    var dy: Double = player.y - entity.y
                    val dz: Double = player.z - entity.z

                    val active =
                        if (data.data.activationDistance == -1) true else dx * dx + dy * dy + dz * dz < data.data.activationDistance.toDouble()
                            .pow(2)

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
