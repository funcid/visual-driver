package experimental.places

import io.netty.buffer.ByteBuf
import readRgb
import readV3
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId

object PlaceController {

    const val ADD_PLACE_CHANNEL = "func:reactive-place"
    const val REMOVE_PLACE_CHANNEL = "func:reactive-place-kill"
    const val UPDATE_PLACE_CHANNEL = "func:reactive-place-update"

    init {
        mod.registerChannel(ADD_PLACE_CHANNEL) {

            PlaceManager.put(
                ReactivePlace(
                    readId(),
                    readRgb(),
                    readV3(),
                    readDouble(),
                    readInt(),
                )
            )
        }

        mod.registerChannel(REMOVE_PLACE_CHANNEL) {
            PlaceManager.remove(readId())
        }

        mod.registerChannel(UPDATE_PLACE_CHANNEL) {

            val uuid = readId()
            val place = PlaceManager.get(uuid) ?: return@registerChannel

            update(place, readInt(), this)
        }
    }

    private fun update(reactivePlace: ReactivePlace, updateId: Int, buf: ByteBuf) {

        when (updateId) {
            1 -> reactivePlace.rgb = buf.readRgb()
            2 -> {
                when (buf.readInt()) {
                    1 -> reactivePlace.location.x = buf.readDouble()
                    2 -> reactivePlace.location.y = buf.readDouble()
                    3 -> reactivePlace.location.z = buf.readDouble()
                }
            }
        }

        PlaceManager.cacheClear()
    }
}