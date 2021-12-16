package me.func.mod.data

import me.func.protocol.DropRare
import org.bukkit.inventory.ItemStack

data class LootDrop(val itemStack: ItemStack, val title: String, val rare: me.func.protocol.DropRare = me.func.protocol.DropRare.COMMON) {
    constructor(
        stack: net.minecraft.server.v1_12_R1.ItemStack,
        title: String,
        rare: me.func.protocol.DropRare
    ) : this(stack.asBukkitMirror(), title, rare)
}
