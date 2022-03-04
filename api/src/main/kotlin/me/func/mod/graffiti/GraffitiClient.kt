package me.func.mod.graffiti

import me.func.protocol.personalization.FeatureUserData
import java.util.*
import java.util.concurrent.CompletableFuture

interface GraffitiClient {

    fun loadUser(uuid: UUID): CompletableFuture<FeatureUserData?>

    fun use(uuid: UUID, pack: UUID, graffiti: UUID): CompletableFuture<Boolean>

    fun buy(uuid: UUID, pack: UUID, price: Int): CompletableFuture<String?>

}