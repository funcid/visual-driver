package experimental.linepointer

import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import readRgb
import readV3
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import java.util.*

object LineController {

    private const val ADD_LINE_CHANNEL = "tensess:add-line"
    private const val REMOVE_LINE_CHANNEL = "tensess:remove-line"
    private const val UPDATE_LINE_CHANNEL = "tensess:update-line"

    init {
        mod.registerChannel(ADD_LINE_CHANNEL) {

            val uuid = readId()
            val hasOrigin = readBoolean()

            LineManager.put(
                LinePointer(
                    uuid,
                    readRgb(),
                    readV3(),
                    readInt(),
                    NetUtil.readUtf8(this),
                    if (hasOrigin) readV3() else null
                )
            )
        }

        mod.registerChannel(REMOVE_LINE_CHANNEL) {

            val uuid = readId()
            LineManager.remove(uuid)
        }

        mod.registerChannel(UPDATE_LINE_CHANNEL) {
            update(readId(), readInt(), this)
        }
    }

    private fun update(uuid: UUID, update: Int, buf: ByteBuf) {

        val line = LineManager.get(uuid) ?: return

        when (update) {
            1 -> line.rgb = buf.readRgb()
            2 -> line.location = buf.readV3()
            3 -> line.origin = if (buf.readBoolean()) buf.readV3() else null
            4 -> line.limitRendering = buf.readInt()
            5 -> line.texture = NetUtil.readUtf8(buf)
        }

        line.update()
    }
}