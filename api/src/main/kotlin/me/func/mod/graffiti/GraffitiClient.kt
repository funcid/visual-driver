package me.func.mod.graffiti

import me.func.protocol.graffiti.FeatureUserData
import java.util.*
import java.util.concurrent.CompletableFuture

interface GraffitiClient {

    fun connect(): GraffitiClient

    fun loadUser(uuid: UUID): CompletableFuture<FeatureUserData?>

    fun use(uuid: UUID, pack: UUID, graffiti: UUID): CompletableFuture<Boolean>

    fun buy(uuid: UUID, pack: UUID, price: Int): CompletableFuture<String?>

}