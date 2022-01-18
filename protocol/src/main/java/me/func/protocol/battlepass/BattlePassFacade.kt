package me.func.protocol.battlepass

data class BattlePassFacade(
    val price: Int,
    val salePercent: Double,
    val tags: List<String>,
)