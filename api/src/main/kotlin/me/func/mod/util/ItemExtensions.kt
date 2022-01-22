package me.func.mod.util

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

fun ItemStack.addNbt(key: String, value: String): ItemStack {
    val copy = CraftItemStack.asNMSCopy(this)
    val tag = copy.tag
    tag.setString(key, value)
    copy.tag = tag
    return CraftItemStack.asBukkitCopy(copy)
}