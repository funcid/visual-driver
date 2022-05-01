package me.func.mod.conversation

import me.func.mod.Anime
import me.func.mod.MOD_LOCAL_DIR_NAME
import me.func.mod.util.warn
import net.minecraft.server.v1_12_R1.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object AutoSendRegistry : Listener {

    private val registry = arrayListOf<String>()

    init {
        Bukkit.getPluginManager().registerEvents(this, Anime.provided)
    }

    @JvmStatic
    fun add(vararg modList: String?) = modList.filterNotNull().forEach { name ->
        val mod = if (name.endsWith(".jar")) name else "$name.jar"
        if (!ModLoader.isLoaded(name)) ModLoader.load("$MOD_LOCAL_DIR_NAME/$mod")
        if (ModLoader.isLoaded(mod)) registry.add(mod)
        else warn("AutoSendRegistry mod add failure!")
    }

    @JvmStatic
    fun remove(modName: String?) = registry.remove(modName)

    @JvmStatic
    fun clear() = registry.clear()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun PlayerJoinEvent.handle() {
        MinecraftServer.SERVER.postToMainThread {
            registry.forEach { ModLoader.send(it, player) }
        }
    }
}