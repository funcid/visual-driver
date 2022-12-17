package experimental.linepointer

import asColor
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import readRgb
import readV3
import ru.cristalix.clientapi.KotlinModHolder.mod
import java.util.*

object LineManager {

    const val UPDATE_PERIOD_MILLIS = 150

    private val lines = hashMapOf<UUID, LinePointer>()
    private var lastUpdate = System.currentTimeMillis()

    init {
        mod.registerHandler<GameLoop> {

            val now = System.currentTimeMillis()

            if (now - lastUpdate > UPDATE_PERIOD_MILLIS) {
                lastUpdate = now

                lines.values.forEach {

                    it.rebase()
                    it.draw()
                }
            }
        }
    }

    fun update(uuid: UUID, update: Int, buf: ByteBuf) {

        val line = lines[uuid] ?: return

        when (update) {
            1 -> line.color = buf.readRgb().asColor()
            2 -> line.location = buf.readV3()
            3 -> line.origin = if (buf.readBoolean()) buf.readV3() else null
            4 -> line.limitRendering = buf.readInt()
            5 -> line.texture = NetUtil.readUtf8(buf)
        }

        line.update()
    }

    fun put(linePointer: LinePointer) {
        lines[linePointer.uuid] = linePointer
    }

    fun remove(uuid: UUID) {

        lines[uuid]?.remove()
        lines.remove(uuid)
    }
}