@file:JvmName("Utils")

package me.func.mod.util

import me.func.mod.Anime
import me.func.mod.conversation.CRAFT_ITEM_TO_NMS
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask

@JvmSynthetic
inline fun unit(block: () -> Unit): Unit = block()

@JvmSynthetic
inline fun <T> T.unit(block: T.() -> Unit): Unit = block()

fun consoleCommand(name: String, consumer: () -> Unit) {
    Bukkit.getCommandMap().register("anime", object : Command(name) {
        override fun execute(sender: CommandSender, var2: String, agrs: Array<out String>): Boolean {
            if (sender is ConsoleCommandSender)
                consumer.invoke()
            return true
        }
    })
}

fun command(name: String, consumer: (Player, Array<out String>) -> Unit) {
    Bukkit.getCommandMap().register("anime", object : Command(name) {
        override fun execute(sender: CommandSender, var2: String, agrs: Array<out String>): Boolean {
            if (sender is Player)
                consumer(sender, agrs)
            return true
        }
    })
}

@JvmOverloads
fun after(ticks: Long = 1, runnable: () -> Unit): BukkitTask =
    Bukkit.getScheduler().runTaskLater(Anime.provided, runnable, ticks)

fun listener(vararg listener: Listener) =
    listener.forEach { Bukkit.getPluginManager().registerEvents(it, Anime.provided) }

fun itemToTexture(item: ItemStack) =
    "minecraft:textures/items/${(CRAFT_ITEM_TO_NMS.invoke(item) as net.minecraft.server.v1_12_R1.ItemStack).name}.png"

/**
 * Копирует итемстак, добавляя в него новый нбт
 * @param key ключ нбт тега
 * @param value значение нбт тега
 * @return Новый Bukkit итемстак, в котором есть нбт тег из аргументов метода
 */
@JvmName("addNbtToItemStack")
fun ItemStack.nbt(key: String, value: String): ItemStack =
    (CRAFT_ITEM_TO_NMS.invoke(this) as net.minecraft.server.v1_12_R1.ItemStack).apply {
        if (tag == null) tag = NBTTagCompound()
        tag.setString(key, value)
    }.asBukkitMirror()
