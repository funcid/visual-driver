package standard.world.banners

import asColor
import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import readRgb
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import ru.cristalix.uiengine.element.TextElement
import java.util.*

object BannersController {

    const val ADD_BANNER_CHANNEL = "banner:new"
    const val UPDATE_BANNER_CHANNEL = "banner:update"
    const val REMOVE_BANNER_CHANNEL = "banner:remove"
    const val TEXT_SIZE_CHANNEL = "banner:size-text"
    const val CLICK_BANNER_CHANNEL = "banner:click"

    init {
        mod.registerChannel(ADD_BANNER_CHANNEL) {
            BannersManager.new(readInt(), this)
        }

        mod.registerChannel(UPDATE_BANNER_CHANNEL) {
            update(readId(), readInt(), this)
        }

        mod.registerChannel(REMOVE_BANNER_CHANNEL) {
            BannersManager.remove(readInt(), this)
        }

        mod.registerChannel(TEXT_SIZE_CHANNEL) {
            BannersManager.textSize(this)
        }
    }

    private fun changeContent(uuid: UUID, content: String) {

        BannersManager.get(uuid)?.let {

            if (it.third.children.size > 2) {
                it.third.removeChild(*it.third.children.filterIsInstance<TextElement>().toTypedArray())
                BannersManager.text(content, it.first, it.third)
            }
        }
    }

    private fun update(uuid: UUID, updateId: Int, buf: ByteBuf) {

        val triple = BannersManager.get(uuid) ?: return

        when (updateId) {
            1 -> changeContent(uuid, NetUtil.readUtf8(buf))
            2 -> {
                when (buf.readInt()) {
                    1 -> triple.second.offset.x = buf.readDouble()
                    2 -> triple.second.offset.y = buf.readDouble()
                    3 -> triple.second.offset.z = buf.readDouble()
                }
            }
            3 -> triple.third.size.y = buf.readDouble()
            4 -> triple.third.size.x = buf.readDouble()
            5 -> triple.third.textureLocation = BannersManager.getTextureLocation(NetUtil.readUtf8(buf))
            6 -> triple.third.color = buf.readRgb().asColor(triple.third.color.alpha)
            7 -> triple.third.color.alpha = buf.readDouble()
        }
    }
}