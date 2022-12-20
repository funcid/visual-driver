package standard.world.reactive

import asColor
import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import readRgb
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import java.util.*

object BannersController {

    const val ADD_BANNER_CHANNEL = "banner:reactive-show"
    const val UPDATE_BANNER_CHANNEL = "banner:reactive-update"
    const val REMOVE_BANNER_CHANNEL = "banner:reactive-remove"
    const val TEXT_SIZE_CHANNEL = "banner:reactive-text"
    const val CLICK_BANNER_CHANNEL = "banner:react-click"

    init {
        mod.registerChannel(ADD_BANNER_CHANNEL) {
            BannersManager.new(this)
        }

        mod.registerChannel(UPDATE_BANNER_CHANNEL) {
            update(readId(), readInt(), this)
        }

        mod.registerChannel(REMOVE_BANNER_CHANNEL) {
            BannersManager.remove(this)
        }

        mod.registerChannel(TEXT_SIZE_CHANNEL) {
            BannersManager.textSize(this)
        }
    }

    private fun update(uuid: UUID, updateId: Int, buf: ByteBuf) {

        val triple = BannersManager.get(uuid) ?: return

        when (updateId) {
            1 -> BannersManager.changeContent(uuid, NetUtil.readUtf8(buf))
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