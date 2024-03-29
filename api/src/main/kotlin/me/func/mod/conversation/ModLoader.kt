package me.func.mod.conversation

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import me.func.atlas.Atlas.download
import me.func.atlas.util.fileLastName
import me.func.mod.Kit
import me.func.mod.MOD_LOCAL_DIR_NAME
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
import java.io.InputStream
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.absolutePathString

object ModLoader {

    val mods: MutableMap<String, ByteBuf?> = HashMap()

    @JvmStatic
    @JvmOverloads
    fun loadFromWeb(fileUrl: String, saveDir: String = MOD_LOCAL_DIR_NAME) =
        load(download(fileUrl, saveDir, cache = false))

    @JvmStatic
    fun loadManyFromWeb(saveDir: String = MOD_LOCAL_DIR_NAME, vararg fileUrls: String) =
        fileUrls.forEach { loadFromWeb(it, saveDir) }

    @JvmStatic
    private fun readModStream(buffer: ByteBuf, stream: InputStream) {
        val data = ByteArray(stream.available())
        IOUtils.readFully(stream, data)
        val tmp = Unpooled.buffer(data.size + 4)
        UtilNetty.writeByteArray(tmp, data)
        buffer.writeBytes(ByteBufUtil.getBytes(tmp, 0, tmp.writerIndex(), false))
    }

    @JvmStatic
    private fun readMod(buffer: ByteBuf, file: File) = readModStream(buffer, FileInputStream(file))

    @JvmStatic
    fun load(path: Path) = load(path.absolutePathString())

    @JvmStatic
    @JvmOverloads
    fun load(name: String, stream: InputStream, overload: Boolean = false) {
        try {
            if (mods.containsKey(name) && !overload) {
                warn("Mod loading abort! Mod `$name` already loaded!")
                return
            }

            mods[name] = Unpooled.buffer().also { readModStream(it, stream) }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun load(file: File?, overload: Boolean = false) {
        if (file == null) return
        load(file.path.fileLastName(), file.inputStream(), overload)
    }

    @JvmStatic
    @JvmOverloads
    fun load(filePath: String, overload: Boolean = false) = load(File(filePath), overload)

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
    fun send(modName: String, player: Player) {
        mods[modName]?.let {
            (player as CraftPlayer).handle.playerConnection.sendPacket(
                PacketPlayOutCustomPayload(MOD_CHANNEL, PacketDataSerializer(it.retainedSlice()))
            )
        }
    }

    @JvmStatic
    fun remove(modName: String) {
        mods.remove(modName)
    }

    @JvmStatic
    fun manyToOne(player: Player) = mods.keys.filter { mod ->
        Kit.values().mapNotNull { it.fromUrl }.none { it.fileLastName() == mod }
    }.forEach { send(it, player) }

    @JvmStatic
    fun oneToMany(modName: String) {
        Bukkit.getOnlinePlayers().forEach { send(modName, it) }
    }

    @JvmStatic
    fun sendAll() {
        Bukkit.getOnlinePlayers().forEach { manyToOne(it) }
    }

    @JvmStatic
    fun isLoaded(name: String) = mods.containsKey(name)

    @JvmStatic
    fun onJoining(vararg mods: String?) = AutoSendRegistry.add(*mods)
}