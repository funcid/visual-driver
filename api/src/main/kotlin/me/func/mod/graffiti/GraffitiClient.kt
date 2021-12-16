package me.func.mod.graffiti

import me.func.protocol.graffiti.UserGraffitiData
import java.util.*
import java.util.concurrent.CompletableFuture

interface GraffitiClient {

    fun connect(): GraffitiClient

    fun loadUser(uuid: UUID): CompletableFuture<UserGraffitiData?>

    fun use(uuid: UUID, pack: UUID, graffiti: UUID): CompletableFuture<Boolean>

    fun buy(uuid: UUID, pack: UUID, price: Int): CompletableFuture<String?>

}