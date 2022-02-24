package me.func.mod.experimental.disguise

import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

internal const val CHANNEL = "anime:disguise"

interface EntityDisguiser {
    companion object {
        val packet: EntityDisguiser = PacketEntityDisguiser()
        val mod: EntityDisguiser = ModEntityDisguiser()
    }

    /**
     * Заменяет тип энтити на клиенте у игроков из [players]
     * @param entity Энтити, тип которой должен быть заменен
     * @param type Новый тип энтити
     * @param players Игроки, у которых тип будет заменен
     */
    fun disguise(entity: Entity, type: EntityType, vararg players: Player)

    /**
     * Восстанавливает прежний тип энтити у игроков из [players]
     * @param entity Энтити, тип которой будет восстановлен
     * @param players Игоки, у которых тип энтити будет восстановлен
     */
    fun reset(entity: Entity, vararg players: Player)
}
