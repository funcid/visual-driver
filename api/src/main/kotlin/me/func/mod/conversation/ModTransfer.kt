package me.func.mod.conversation

import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import io.netty.handler.codec.EncoderException
import net.minecraft.server.v1_12_R1.*
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.GlobalSerializers
import java.io.DataOutput
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.*

val LOOKUP: MethodHandles.Lookup = MethodHandles.publicLookup()
val WRITE_ITEM: MethodHandle = try {
    LOOKUP.findVirtual(
        PacketDataSerializer::class.java,
        "writeItem",
        MethodType.methodType(PacketDataSerializer::class.java, net.minecraft.server.v1_12_R1.ItemStack::class.java)
    )
} catch (_: Throwable) {
    LOOKUP.findVirtual(
        PacketDataSerializer::class.java,
        "a",
        MethodType.methodType(PacketDataSerializer::class.java, net.minecraft.server.v1_12_R1.ItemStack::class.java)
    )
}

val CRAFT_ITEM_TO_NMS: MethodHandle by lazy {
    try {
        LOOKUP.findStatic(
            CraftItemStack::class.java,
            "asNMSCopy",
            MethodType.methodType(net.minecraft.server.v1_12_R1.ItemStack::class.java, ItemStack::class.java)
        )
    } catch (_: Throwable) {
        LOOKUP.findStatic(
            ItemStack::class.java,
            "asNMSCopy",
            MethodType.methodType(net.minecraft.server.v1_12_R1.ItemStack::class.java, ItemStack::class.java)
        )
    }
}

class ModTransfer(val serializer: PacketDataSerializer = PacketDataSerializer(Unpooled.buffer())) {

    constructor(vararg data: Any) : this() {
        for (info in data) {
            when (info) {
                is String -> string(info)
                is ItemStack -> item(info)
                is net.minecraft.server.v1_12_R1.ItemStack -> item(info)
                is Byte -> byte(info)
                is Int -> integer(info)
                is Short -> short(info)
                is Boolean -> boolean(info)
                is Double -> double(info)
                is NBTTagCompound -> nbt(info)
            }
        }
    }

    fun json(string: Any) = apply { string(GlobalSerializers.toJson(string)) }

    fun varInt(integer: Int) = apply { NetUtil.writeVarInt(integer, serializer) }

    fun putString(string: String) = apply { NetUtil.writeUtf8(string, serializer) }

    fun string(string: String) = apply { putString(string) }

    fun byteArray(vararg byte: Byte) = apply { serializer.writeBytes(byte) }

    fun item(item: net.minecraft.server.v1_12_R1.ItemStack) = apply { WRITE_ITEM.invoke(serializer, item) }

    fun item(item: ItemStack) = item(CRAFT_ITEM_TO_NMS.invoke(item) as net.minecraft.server.v1_12_R1.ItemStack)

    fun nbt(nbt: NBTTagCompound) = apply { writeNbtCompound(serializer, nbt) }

    fun nbt(item: ItemStack) = nbt(CRAFT_ITEM_TO_NMS.invoke(item) as net.minecraft.server.v1_12_R1.ItemStack)

    fun nbt(item: net.minecraft.server.v1_12_R1.ItemStack) =
        nbt(item.tag ?: NBTTagCompound().apply { RuntimeException("Warning: Tag is null!").printStackTrace() })

    fun integer(integer: Int) = apply { serializer.writeInt(integer) }

    @JvmName("putLong")
    fun long(long: Long) = apply { serializer.writeLong(long) }

    fun short(short: Short) = apply { serializer.writeShort(short.toInt()) }

    fun byte(byte: Byte) = apply { serializer.writeByte(byte.toInt()) }

    @JvmName("putDouble")
    fun double(double: Double) = apply { serializer.writeDouble(double) }

    @JvmName("putBoolean")
    fun boolean(boolean: Boolean) = apply { serializer.writeBoolean(boolean) }

    fun uuid(uuid: UUID) = apply { serializer.writeUuid(uuid) }

    fun send(channel: String, vararg players: Player?): Unit = send(channel, players.asIterable())

    fun send(channel: String, players: Iterable<Player?>): Unit =
        players.filterIsInstance<CraftPlayer>().forEach {
            it.handle.playerConnection.networkManager.sendPacket(
                PacketPlayOutCustomPayload(channel, PacketDataSerializer(serializer.retainedSlice()))
            )
        }

    fun writeNbtCompound(data: PacketDataSerializer, nbt: NBTTagCompound?): PacketDataSerializer {
        if (nbt == null) {
            data.writeByte(0)
        } else {
            try {
                NBTCompressedStreamTools.a(nbt as NBTBase, ByteBufOutputStream(data) as DataOutput)
            } catch (var3: Exception) {
                throw EncoderException(var3)
            }
        }
        return data
    }
}
