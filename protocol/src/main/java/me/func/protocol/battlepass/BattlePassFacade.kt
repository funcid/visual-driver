package me.func.protocol.battlepass

class BattlePassFacade(
    var price: Int = 0,
    var salePercent: Double = 0.0,
    var tags: MutableList<String> = mutableListOf(),
)