package me.func.mod.conversation

import me.func.atlas.Atlas
import me.func.atlas.util.fileLastName
import me.func.mod.MOD_LOCAL_DIR_NAME
import me.func.mod.util.after
import me.func.mod.util.warn
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object AutoSendRegistry : Listener {

    private val registry = arrayListOf<String>()

    @JvmStatic
    fun add(vararg modList: String?) = modList.filterNotNull().onEach { name ->
        val mod = (if (name.endsWith(".jar")) name else "$name.jar").fileLastName()
        if (name.startsWith("http")) Atlas.download(name)
        if (!ModLoader.isLoaded(mod)) ModLoader.load("$MOD_LOCAL_DIR_NAME/$mod")
        if (ModLoader.isLoaded(mod)) registry.add(mod)
        else warn("AutoSendRegistry mod add failure!")
    }

    @JvmStatic
    fun remove(modName: String?) = registry.remove(modName)

    @JvmStatic
    fun clear() = registry.clear()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun PlayerJoinEvent.handle() {
        after {
            registry.forEach { ModLoader.send(it, player) }
        }
    }
}