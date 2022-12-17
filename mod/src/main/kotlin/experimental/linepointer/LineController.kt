package experimental.linepointer

import asColor
import dev.xdark.feder.NetUtil
import readRgb
import readV3
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId

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
                    readRgb().asColor(),
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

            LineManager.update(readId(), readInt(), this)
        }
    }
}