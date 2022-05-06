package me.func.mod.conversation

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import me.func.mod.Kit
import me.func.mod.MOD_LOCAL_DIR_NAME
import me.func.mod.util.fileLastName
import me.func.mod.util.warn
import net.minecraft.server.v1_12_R1.PacketDataSerializer
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload
import org.apache.commons.io.IOUtils
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import ru.cristalix.core.display.DisplayChannels.MOD_CHANNEL
import ru.cristalix.core.util.UtilNetty
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.absolutePathString

object ModLoader {

    val mods: MutableMap<String, ByteBuf?> = HashMap()

    @JvmStatic
    @JvmOverloads
    fun download(fileUrl: String, saveDir: String = MOD_LOCAL_DIR_NAME): String {
        return try {
            val dir = Paths.get(saveDir)
            if (Files.notExists(dir))
                Files.createDirectory(dir)

            val website = URL(fileUrl)
            val file = File(saveDir + "/" + fileUrl.fileLastName())
            file.createNewFile()
            website.openStream().use { `in` ->
                Files.copy(
                    `in`,
                    file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            file.path
        } catch (exception: Exception) {
            warn(exception.message ?: "Download failure! File: $fileUrl, directory: $saveDir")
            ""
        }
    }

    @JvmStatic
    @JvmOverloads
    fun loadFromWeb(fileUrl: String, saveDir: String = MOD_LOCAL_DIR_NAME) =
        load(download(fileUrl, saveDir))

    @JvmStatic
    @JvmOverloads
    fun loadManyFromWeb(saveDir: String = MOD_LOCAL_DIR_NAME, vararg fileUrls: String) =
        fileUrls.forEach { loadFromWeb(it, saveDir) }

    @JvmStatic
    private fun readMod(buffer: ByteBuf, file: File) {
        FileInputStream(file).use { stream ->
            val data = ByteArray(stream.available())
            IOUtils.readFully(stream, data)
            val tmp = Unpooled.buffer(data.size + 4)
            UtilNetty.writeByteArray(tmp, data)
            buffer.writeBytes(ByteBufUtil.getBytes(tmp, 0, tmp.writerIndex(), false))
        }
    }

    @JvmStatic
    fun load(path: Path) = load(path.absolutePathString())

    @JvmStatic
    @JvmOverloads
    fun load(filePath: String?, overload: Boolean = false) {
        if (filePath.isNullOrEmpty())
            return
        try {
            val key = filePath.split('/').last()

            if (mods.containsKey(key) && !overload) {
                warn("Mod loading abort! Mod `$key` already loaded!")
                return
            }

            mods[key] = Unpooled.buffer().also { readMod(it, File(filePath)) }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @JvmStatic
    fun loadAll(path: Path) = Files.walkFileTree(path, object :
        SimpleFileVisitor<Path>() {

        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            if (file.toString().endsWith(".jar") && !Files.isDirectory(file))
                load(file.absolutePathString())
            return super.visitFile(file, attrs)
        }
    })

    @JvmStatic
    fun loadAll(dirPath: String) = File("./$dirPath").listFiles()?.apply {
        if (size > 100) {
            warn("To many files in dir: $dirPath")
            return@apply
        }
        forEach { load(it.path) }
    }

    @JvmStatic
    fun send(modName: String, player: Player) = mods[modName]?.let {
        (player as CraftPlayer).handle.playerConnection.sendPacket(
            PacketPlayOutCustomPayload(MOD_CHANNEL, PacketDataSerializer(it.retainedSlice()))
        )
    }

    @JvmStatic
    fun remove(modName: String) =
        mods.remove(modName)

    @JvmStatic
    fun manyToOne(player: Player) =
        mods.keys.filter { mod ->
            Kit.values()
                .filter { it.fromUrl != null }
                .none { it.fromUrl!!.split('/').last() == mod }
        }.forEach { send(it, player) }

    @JvmStatic
    fun oneToMany(modName: String) = Bukkit.getOnlinePlayers().forEach { send(modName, it) }

    @JvmStatic
    fun sendAll() = Bukkit.getOnlinePlayers().forEach { manyToOne(it) }

    @JvmStatic
    fun isLoaded(name: String) = mods.containsKey(name)

    @JvmStatic
    fun onJoining(vararg mods: String?) = AutoSendRegistry.add(*mods)
}