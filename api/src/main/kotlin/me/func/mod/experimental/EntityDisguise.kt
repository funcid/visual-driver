package me.func.mod.experimental

import me.func.mod.conversation.ModTransfer
import org.apiguardian.api.API
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

@API(status = API.Status.EXPERIMENTAL)
object EntityDisguise {
    private const val channel = "anime:disguise"

    /**
     * Заменяет тип энтити на клиенте у игроков из [players]
     * @param entity Энтити, тип которой должен быть заменен
     * @param type Новый тип энтити
     * @param players Игроки, у которых тип будет заменен
     */
    @JvmStatic // TODO: Remove it
    fun disguise(entity: Entity, type: EntityType, vararg players: Player) {
        ModTransfer(
            entity.entityId, // Entity ID
            false, // Reset
            type.typeId // Entity Type
        ).run { players.forEach { send(channel, it) } }
    }

    /**
     * Заменяет тип энтити на клиенте у игроков из [players]
     * @param entity Энтити, тип которой должен быть заменен
     * @param type Новый тип энтити
     * @param players Игроки, у которых тип будет заменен
     */
    @JvmStatic
    fun disguise(entity: Entity, type: EntityType, players: List<Player>) = disguise(
        entity, type, *players.toTypedArray()
    )

    /**
     * Восстанавливает прежний тип энтити у игроков из [players]
     * @param entity Энтити, тип которой будет восстановлен
     * @param players Игоки, у которых тип энтити будет восстановлен
     */
    @JvmStatic
    fun reset(entity: Entity, vararg players: Player) {
        ModTransfer(
            entity.entityId, // Entity ID
            true // Reset
        ).run { players.forEach { send(channel, it) } }
    }

    /**
     * Восстанавливает прежний тип энтити у игроков из [players]
     * @param entity Энтити, тип которой будет восстановлен
     * @param players Игоки, у которых тип энтити будет восстановлен
     */
    @JvmStatic
    fun reset(entity: Entity, players: List<Player>) = reset(entity, *players.toTypedArray())
}
