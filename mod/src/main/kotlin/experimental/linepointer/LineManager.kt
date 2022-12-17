package experimental.linepointer

import dev.xdark.clientapi.event.lifecycle.GameLoop
import ru.cristalix.clientapi.KotlinModHolder.mod
import java.util.*

object LineManager {

    const val UPDATE_PERIOD_MILLIS: Long = 150

    private val lines = hashMapOf<UUID, LinePointer>()
    private var lastUpdate = System.currentTimeMillis()

    init {
        mod.registerHandler<GameLoop> {

            val now = System.currentTimeMillis()

            if (now - lastUpdate > UPDATE_PERIOD_MILLIS) {
                lastUpdate = now

                lines.values.forEach {

                    when (it.origin == null) {
                        true -> it.rebase()
                        false -> {
                            it.rebase(1)
                            it.animation()
                        }
                    }

                    it.draw()
                    it.changeVisibility()
                }
            }
        }
    }

    fun get(uuid: UUID) = lines[uuid]

    fun put(linePointer: LinePointer) {
        lines[linePointer.uuid] = linePointer
    }

    fun remove(uuid: UUID) {

        lines[uuid]?.remove()
        lines.remove(uuid)
    }
}