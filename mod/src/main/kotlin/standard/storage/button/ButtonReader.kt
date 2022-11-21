package standard.storage.button

import dev.xdark.clientapi.item.ItemTools
import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import readColoredUtf8
import readRgb

object ButtonReader {

    fun readIcons(buffer: ByteBuf) = MutableList(buffer.readInt()) {
        val data = if (buffer.readBoolean()) StorageItemStack(ItemTools.read(buffer)) // item
        else StorageItemTexture(NetUtil.readUtf8(buffer)) // texture

        data.apply {
            price = buffer.readLong() // price
            priceText = buffer.readColoredUtf8()
            title = buffer.readColoredUtf8() // item title
            description = buffer.readColoredUtf8() // item description
            hint = buffer.readColoredUtf8() // item hint
            hoverText = buffer.readColoredUtf8() // item hover desc
            command = buffer.readColoredUtf8() // command
            vault = buffer.readColoredUtf8() // vault
            backgroundColor = buffer.readRgb() // color
            enabled = buffer.readBoolean() // enabled button
            sale = buffer.readInt()
        }
    }

}