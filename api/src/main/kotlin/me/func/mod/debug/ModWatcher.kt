package me.func.mod.debug

import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.log
import org.bukkit.Bukkit
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.util.*
import java.util.jar.JarFile
import kotlin.io.path.absolutePathString

internal object ModWatcher {

    val TEST_PATH: Path = Paths.get(System.getenv("MOD_TEST_PATH") ?: "anime-test")
    private val MODS: MutableMap<String, String> = hashMapOf()

    init {
        log("Initializing ModWatcher")

        if (!Files.exists(TEST_PATH) || !Files.isDirectory(TEST_PATH))
            Files.createDirectory(TEST_PATH)

        val watchService = FileSystems.getDefault().newWatchService()
        TEST_PATH.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)

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
                    val modName: String = path.fileName.toString()
                    val resolved = TEST_PATH.resolve(path)

                    if (!modName.endsWith(".jar")) continue

                    if (event.kind() === ENTRY_DELETE) {
                        ModLoader.remove(modName)
                        MODS.entries.removeIf { it.value == modName }
                        continue
                    }

                    JarFile(resolved.absolutePathString()).use {
                        val props = loadProps(it)

                        (props["name"] as? String)?.let { it1 ->
                            if (MODS[it1] == null) {
                                ModLoader.load(resolved.absolutePathString())
                                ModLoader.oneToMany(modName)
                                MODS[it1] = modName
                            } else {
                                reload(props)
                            }
                        }
                    }
                }

                key.reset()
            }
        }.start()
    }

    private fun loadProps(jarFile: JarFile): Properties =
        Properties().apply {
            load(jarFile.getInputStream(jarFile.getEntry("mod.properties")))
        }


    private fun reload(props: Properties) {
        val main = props["main"] as? String ?: return

        Bukkit.getOnlinePlayers().forEach {
            ModTransfer()
                .string(main)
                .apply {
                    send("sdkreload", it)
                    send("sdk4reload", it)
                }
        }
    }
}