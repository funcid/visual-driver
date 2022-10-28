package me.func.mod.conversation.data

import me.func.protocol.data.rare.DropRare
import org.bukkit.inventory.ItemStack

open class LootDrop(open var itemStack: ItemStack, var title: String, var rare: DropRare = DropRare.COMMON) {
    constructor(
        stack: net.minecraft.server.v1_12_R1.ItemStack,
        title: String,
        rare: DropRare
    ) : this(stack.asBukkitMirror(), title, rare)

    constructor(stack: ItemStack, title: String, rare: String) : this(stack, title) {
        customRare = rare
    }

    constructor(
        stack: net.minecraft.server.v1_12_R1.ItemStack,
        title: String,
        rare: String
    ) : this(stack.asBukkitMirror(), title, rare)

    var customRare: String? = null
}
