package me.func.mod.debug

import me.func.mod.MOD_LOCAL_TEST_DIR_NAME
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.func.mod.util.dir
import me.func.mod.util.fileLastName
import me.func.mod.util.log
import me.func.mod.util.warn
import org.bukkit.Bukkit
import ru.cristalix.core.formatting.Formatting
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.StandardWatchEventKinds.OVERFLOW
import java.nio.file.WatchKey
import java.util.Properties
import java.util.jar.JarFile
import kotlin.concurrent.thread
import kotlin.io.path.absolutePathString

fun interface Subscriber {
    fun accept()
}

internal object ModWatcher {

    val testingPath: Path = dir(MOD_LOCAL_TEST_DIR_NAME)
    val cooldown = arrayListOf<String>()
    private var onReload: Subscriber? = null

    fun onReload(subscriber: Subscriber) = apply { onReload = subscriber }

    init {
        val watchService = FileSystems.getDefault().newWatchService()
        testingPath.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)

        thread {
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
        }
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
            onReload?.accept()
        }
    }
}