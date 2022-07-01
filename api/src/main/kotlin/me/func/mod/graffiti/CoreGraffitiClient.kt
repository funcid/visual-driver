package me.func.mod.graffiti

import me.func.protocol.graffiti.packet.GraffitiBuyPackage
import me.func.protocol.graffiti.packet.GraffitiLoadUserPackage
import me.func.protocol.graffiti.packet.GraffitiUsePackage
import me.func.protocol.personalization.FeatureUserData
import me.func.protocol.personalization.packet.StickersAvailablePackage
import ru.cristalix.core.network.Capability
import ru.cristalix.core.network.ISocketClient
import java.util.UUID
import java.util.concurrent.CompletableFuture

class CoreGraffitiClient : GraffitiClient {

    init {
        ISocketClient.get().registerCapabilities(
            Capability.builder()
                .className(GraffitiBuyPackage::class.java.name)
                .notification(true)
                .build(),
            Capability.builder()
                .className(GraffitiUsePackage::class.java.name)
                .notification(true)
                .build(),
            Capability.builder()
                .className(GraffitiLoadUserPackage::class.java.name)
                .notification(true)
                .build(),
            Capability.builder()
                .className(StickersAvailablePackage::class.java.name)
                .notification(true)
                .build()
        )
    }

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