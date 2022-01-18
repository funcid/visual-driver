package me.func.protocol.battlepass

import java.util.*

abstract class BattlePassPage(
    open val uuid: UUID,
    open var requiredExp: Int,
    open var quests: List<String>
)