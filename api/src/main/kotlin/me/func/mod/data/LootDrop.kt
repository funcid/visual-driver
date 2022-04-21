package me.func.mod.data

import me.func.protocol.DropRare
import org.bukkit.inventory.ItemStack

data class LootDrop(val itemStack: ItemStack, val title: String, val rare: DropRare = DropRare.COMMON) {
    constructor(
        stack: net.minecraft.server.v1_12_R1.ItemStack,
        title: String,
        rare: DropRare
    ) : this(stack.asBukkitMirror(), title, rare)
}
