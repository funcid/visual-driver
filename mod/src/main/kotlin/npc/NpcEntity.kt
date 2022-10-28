package npc

import dev.xdark.clientapi.entity.EntityLivingBase
import me.func.protocol.world.npc.NpcData
import java.util.UUID

class NpcEntity(
    val uuid: UUID,
    val data: NpcData,
    val entity: EntityLivingBase
)
