import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.UIEngine

object Disguise {
    private val minecraft = UIEngine.clientApi.minecraft()
    private val entityProvider = UIEngine.clientApi.entityProvider()

    init {
        App::class.mod.registerChannel("kamillaova:disguise") {
            val world = minecraft.world

            val entityId = readInt() // Entity ID
            val entity = world.getEntity(entityId)

            if (readBoolean()) { // Reset
                entity.renderingEntity = entity
            } else {
                val entityType = readShort() // Entity Type
                entity.renderingEntity = entityProvider.newEntity(entityType.toInt(), world)
            }
        }
    }
}
