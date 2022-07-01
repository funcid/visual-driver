package lootbox

import dev.xdark.clientapi.item.ItemStack

class Loot(
    @JvmField val item: ItemStack,
    @JvmField val name: String,
    @JvmField val rarity: Rarity
)
