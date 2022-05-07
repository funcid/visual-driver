package me.func.mod.debug

import me.func.mod.Anime
import me.func.mod.Anime.reload
import me.func.mod.MOD_LOCAL_TEST_DIR_NAME
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.*
import org.bukkit.Bukkit
import ru.cristalix.core.formatting.Formatting
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.util.*
import java.util.jar.JarFile
import kotlin.io.path.absolutePathString

internal object ModWatcher {

    val testingPath: Path = dir(MOD_LOCAL_TEST_DIR_NAME)
    val cooldown = arrayListOf<String>()

    init {
        val watchService = FileSystems.getDefault().newWatchService()
        testingPath.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)

        Thread {
            while (true) {
                val key: WatchKey = try {
                    watchService.take()
                } catch (e: InterruptedException) {
                    continue
                }

                for (event in key.pollEvents()) {
                    if (event.kind() === OVERFLOW) continue

                    val path = event.context() as Path
                    val modName: String = path.fileLastName()

                    if (cooldown.contains(modName))
                        continue

                    if (!modName.endsWith(".jar")) continue
                    ModLoader.remove(modName)

                    if (event.kind() === ENTRY_DELETE)
                        continue

                    tryLoadUpdateMod(modName, testingPath.resolve(path))
                }
                key.reset()
            }
        }.start()
    }

    private fun tryLoadUpdateMod(modName: String, resolved: Path, tries: Int = 10) {
        if (cooldown.contains(modName))
            return

        if (tries < 0) {
            warn("Mod debug update failure! 10 tries has left.")
            return
        }
        try {
            JarFile(resolved.absolutePathString()).use { file ->
                val props = loadProps(file)

                (props["name"] as? String)?.let {
                    ModLoader.load(resolved.absolutePathString(), true)
                    ModLoader.oneToMany(modName)
                    reload(props)

                    cooldown.add(modName)
                    after(100) { cooldown.remove(modName) }
                }
            }
        } catch (_: Throwable) {
            after(5) { tryLoadUpdateMod(modName, resolved, tries - 1) }
        }
    }

    private fun loadProps(jarFile: JarFile) = Properties().apply {
        load(jarFile.getInputStream(jarFile.getEntry("mod.properties")))
    }

    private fun reload(props: Properties) {
        ModTransfer(props["main"] as? String ?: return).apply {
            log("Mod `${props["name"]}` successfully reloaded!")
            Bukkit.getOnlinePlayers().forEach {
                send("sdkreload", it)
                send("sdk4reload", it)
                it.sendMessage(Formatting.fine("Мод ${props["name"]} перезагружен!"))
            }
        }
    }
}