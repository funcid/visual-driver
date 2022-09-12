package standard.world

import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.Color
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.sphere
import standard.util.ClientApiAllowedLock
import java.util.UUID

class SphereManager {
    private val contents: MutableMap<UUID, Context3D> = hashMapOf()
    private val lock = ClientApiAllowedLock()

    init {
        mod.registerChannel("fiwka:sphere") {
            when (readInt()) {
                0 -> {
                    drawSphere(
                        UUID(readLong(), readLong()),
                        readDouble(),
                        readDouble(),
                        readDouble(),
                        Color(readInt(), readInt(), readInt(), readDouble()),
                        readDouble()
                    )
                }
                1 -> {
                    drawSphere(
                        UUID(readLong(), readLong()),
                        readDouble(),
                        readDouble(),
                        readDouble(),
                        Color(readInt(), readInt(), readInt(), readDouble()),
                        readDouble(),
                        readDouble(),
                        readDouble()
                    )
                }
                2 -> {
                    teleportTo(
                        UUID(readLong(), readLong()),
                        readDouble(),
                        readDouble(),
                        readDouble(),
                    )
                }
                3 -> {
                    moveTo(
                        UUID(readLong(), readLong()),
                        readDouble(),
                        readDouble(),
                        readDouble(),
                        readDouble(),
                    )
                }
                4 -> {
                    removeSphere(
                        UUID(readLong(), readLong()),
                    )
                }
            }
        }
    }

    fun drawSphere(uuid: UUID, x: Double, y: Double, z: Double, color: Color, radius: Double) =
        drawSphere(uuid, x, y, z, color, radius, radius, radius)

    fun drawSphere(uuid: UUID, x: Double, y: Double, z: Double, color: Color, sX: Double, sY: Double, sZ: Double) {
        lock.lock()

        val context = Context3D(V3(x, y, z))

        context.addChild(sphere {
            size.x = sX
            size.y = sY
            size.z = sZ

            this.color = color
        })

        contents[uuid] = context
        UIEngine.worldContexts.add(context)

        lock.unlock()
    }

    fun teleportTo(uuid: UUID, x: Double, y: Double, z: Double) =
        contents[uuid]?.let {
            lock.lock()

            it.offset.x = x
            it.offset.y = y
            it.offset.z = z

            lock.unlock()
        }

    fun moveTo(uuid: UUID, x: Double, y: Double, z: Double, time: Double) =
        contents[uuid]?.let {
            lock.lock()

            it.animate(time) {
                offset.x = x
                offset.y = y
                offset.z = z

                lock.unlock()
            }
        }

    fun removeSphere(uuid: UUID) = contents[uuid]?.let {
        lock.lock()

        UIEngine.worldContexts.remove(it)
        contents.remove(uuid)

        lock.unlock()
    }
}
