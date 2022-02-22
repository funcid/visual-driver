package me.func.mod.graffiti

import me.func.protocol.graffiti.FeatureUserData
import me.func.protocol.graffiti.packet.GraffitiBuyPackage
import me.func.protocol.graffiti.packet.GraffitiLoadUserPackage
import me.func.protocol.graffiti.packet.GraffitiUsePackage
import ru.cristalix.core.network.ISocketClient
import java.util.*
import java.util.concurrent.CompletableFuture

class CoreGraffitiClient : GraffitiClient {

    override fun connect(): GraffitiClient =
        TODO("Not yet implemented")

    override fun loadUser(uuid: UUID): CompletableFuture<FeatureUserData?> =
        ISocketClient.get()
            .writeAndAwaitResponse<GraffitiLoadUserPackage>(GraffitiLoadUserPackage(uuid))
            .thenApply { it.data }

    override fun use(uuid: UUID, pack: UUID, graffiti: UUID): CompletableFuture<Boolean> =
        ISocketClient.get()
            .writeAndAwaitResponse<GraffitiUsePackage>(GraffitiUsePackage(uuid, pack, graffiti))
            .thenApply { it.success }

    override fun buy(uuid: UUID, pack: UUID, price: Int): CompletableFuture<String?> =
        ISocketClient.get()
            .writeAndAwaitResponse<GraffitiBuyPackage>(GraffitiBuyPackage(uuid, pack, price))
            .thenApply { it.errorMessage }
}