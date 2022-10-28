package me.func.mod.graffiti

import me.func.mod.service.Services.socketClient
import me.func.protocol.graffiti.packet.GraffitiBuyPackage
import me.func.protocol.graffiti.packet.GraffitiLoadUserPackage
import me.func.protocol.graffiti.packet.GraffitiUsePackage
import me.func.protocol.personalization.FeatureUserData
import me.func.protocol.personalization.packet.StickersAvailablePackage
import ru.cristalix.core.network.Capability
import java.util.UUID
import java.util.concurrent.CompletableFuture

class CoreGraffitiClient : GraffitiClient {

    override fun loadUser(uuid: UUID): CompletableFuture<FeatureUserData?> =
        socketClient
            .writeAndAwaitResponse<GraffitiLoadUserPackage>(GraffitiLoadUserPackage(uuid))
            .thenApply { it.data }

    override fun use(uuid: UUID, pack: UUID, graffiti: UUID): CompletableFuture<Boolean> =
        socketClient
            .writeAndAwaitResponse<GraffitiUsePackage>(GraffitiUsePackage(uuid, pack, graffiti))
            .thenApply { it.success }

    override fun buy(uuid: UUID, pack: UUID, price: Int): CompletableFuture<String?> =
        socketClient
            .writeAndAwaitResponse<GraffitiBuyPackage>(GraffitiBuyPackage(uuid, pack, price))
            .thenApply { it.errorMessage }
}
