package me.func.mod.conversation

import dev.xdark.feder.NetUtil
import dev.xdark.paper.network.NetworkHooks
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

class ModTransfer(private val serializer: PacketDataSerializer = PacketDataSerializer(Unpooled.buffer())) {

    constructor(vararg data: Any) : this() {
        for (info in data) {
            when(info) {
                is String -> string(info)
                is ItemStack -> item(info)
                is net.minecraft.server.v1_12_R1.ItemStack -> item(info)
                is Int -> integer(info)
                is Boolean -> boolean(info)
                is Double -> double(info)
                is NBTTagCompound -> nbt(info)
            }
        }
    }

    fun json(string: Any) = this.apply { string(GlobalSerializers.toJson(string)) }

    fun varInt(integer: Int) = this.apply { NetUtil.writeVarInt(integer, serializer) }

    fun putString(string: String) = this.apply { NetUtil.writeUtf8(string, serializer) }

    fun string(string: String) = this.apply { putString(string) }

    fun byteArray(vararg byte: Byte) = this.apply { serializer.writeBytes(byte) }

    fun item(item: net.minecraft.server.v1_12_R1.ItemStack) = this.apply { serializer.writeItem(item) }

    fun item(item: ItemStack) = this.apply { item(CraftItemStack.asNMSCopy(item)) }

    fun nbt(nbt: NBTTagCompound) = this.apply { writeNbtCompound(serializer, nbt) }

    fun nbt(item: ItemStack) = this.apply { nbt(CraftItemStack.asNMSCopy(item)) }

    fun nbt(item: net.minecraft.server.v1_12_R1.ItemStack) { this.apply { nbt(item.tag) } }

    fun integer(integer: Int) = this.apply { serializer.writeInt(integer) }

    @JvmName("putDouble")
    fun double(double: Double) = this.apply { serializer.writeDouble(double) }

    @JvmName("putBoolean")
    fun boolean(boolean: Boolean) = this.apply { serializer.writeBoolean(boolean) }

    fun send(channel: String?, player: Player?) {
        if (player == null)
            return

        serializer.a = serializer.retainedSlice()

        (player as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutCustomPayload(channel, serializer))
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