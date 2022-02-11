package me.func.lootbox

import dev.xdark.clientapi.item.ItemStack

data class Loot(val item: ItemStack, val name: String, val rarity: Rarity)