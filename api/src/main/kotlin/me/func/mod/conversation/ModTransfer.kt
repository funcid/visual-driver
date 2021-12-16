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
            }
        }
    }

    fun json(string: Any) = this.apply { string(GlobalSerializers.toJson(string)) }

    fun putString(string: String) = this.apply { NetUtil.writeUtf8(string, serializer) }

    fun string(string: String) = this.apply { putString(string) }

    fun item(item: net.minecraft.server.v1_12_R1.ItemStack) = this.apply { writeItem(serializer, item) }

    fun item(item: ItemStack) = this.apply { writeItem(serializer, CraftItemStack.asNMSCopy(item)) }

    fun integer(integer: Int) = this.apply { serializer.writeInt(integer) }

    @JvmName("putDouble")
    fun double(double: Double) = this.apply { serializer.writeDouble(double) }

    @JvmName("putBoolean")
    fun boolean(boolean: Boolean) = this.apply { serializer.writeBoolean(boolean) }

    fun send(channel: String?, player: Player?) {
        if (player == null)
            return

        (player as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutCustomPayload(channel, serializer))
    }

    fun writeItem(data: PacketDataSerializer, input: net.minecraft.server.v1_12_R1.ItemStack): PacketDataSerializer {
        var item = input
        item = NetworkHooks.rewriteItem(item)
        if (!item.isEmpty && item.getItem() != null) {
            data.writeShort(Item.getId(item.getItem())).writeByte(item.getCount()).writeShort(item.data)
            var nbttagcompound: NBTTagCompound? = null
            if (item.getItem().usesDurability() || item.getItem().p()) {
                item = item.cloneItemStack()
                CraftItemStack.setItemMeta(item, CraftItemStack.getItemMeta(item))
                nbttagcompound = item.getTag()
            }
            writeNbtCompound(data, nbttagcompound)
        } else {
            data.writeShort(-1)
        }
        return data
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