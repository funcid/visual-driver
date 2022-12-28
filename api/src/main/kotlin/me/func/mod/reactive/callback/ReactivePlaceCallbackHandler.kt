package me.func.mod.reactive.callback

import me.func.mod.Anime
import me.func.mod.conversation.broadcast.SubscribeVerifier
import me.func.mod.reactive.ReactivePlace
import me.func.mod.util.readUuid
import me.func.mod.util.safe
import kotlin.math.pow

object ReactivePlaceCallbackHandler {

    init {
        Anime.createReader("server:place-signal") { player, buffer ->

            safe {

                val uuid = buffer.readUuid()
                val place = SubscribeVerifier.providers[uuid] ?: return@safe

                if (place !is ReactivePlace) return@safe

                val inside = (place.x - player.location.x).pow(2) + (place.z - player.location.z).pow(2) <= place.radius.pow(2)

                if (inside) place.onEntire?.accept(player)
                else place.onLeave?.accept(player)
            }
        }
    }

}