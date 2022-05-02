import dev.xdark.clientapi.entity.EntityLivingBase
import me.func.protocol.npc.NpcData
import java.util.*


class NpcEntity(
    val uuid: UUID,
    var data: NpcData,
    var entity: EntityLivingBase? = null
)