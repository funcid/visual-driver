package npc

import dev.xdark.clientapi.entity.EntityLivingBase
import me.func.protocol.npc.NpcData
import java.util.UUID

class NpcEntity(
    @JvmField val uuid: UUID,
    @JvmField var data: NpcData,
    @JvmField var entity: EntityLivingBase? = null
)
