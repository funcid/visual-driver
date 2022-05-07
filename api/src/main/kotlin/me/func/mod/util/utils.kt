package me.func.mod.util

import me.func.mod.Anime
import me.func.mod.conversation.CRAFT_ITEM_TO_NMS
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask

fun command(name: String, consumer: (Player, Array<out String>) -> Unit) =
    Bukkit.getCommandMap().register("anime", object : Command(name) {
        override fun execute(sender: CommandSender, var2: String, agrs: Array<out String>): Boolean {
            if (sender is Player)
                consumer(sender, agrs)
            return true
        }
    })

@JvmOverloads
fun after(ticks: Long = 1, runnable: () -> Unit): BukkitTask =
    Bukkit.getScheduler().runTaskLater(Anime.provided, { runnable.invoke() }, ticks)

fun listener(vararg listener: Listener) =
    listener.onEach { Bukkit.getPluginManager().registerEvents(it, Anime.provided) }

fun ItemStack.nbt(key: String, value: String) =
    (CRAFT_ITEM_TO_NMS.invoke(this) as net.minecraft.server.v1_12_R1.ItemStack).apply {
        if (tag == null) tag = NBTTagCompound()
        tag.setString(key, value)
    }