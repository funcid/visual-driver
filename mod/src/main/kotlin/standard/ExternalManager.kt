package standard

import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.clientapi.KotlinMod

context(KotlinMod)
class ExternalManager {
    private val textures = arrayListOf<ResourceLocation>()

    init {
        registerChannel("func:load-paths") {
            val count = readInt()
            val paths = arrayListOf<String>()

            repeat(count) {
                paths.add(NetUtil.readUtf8(this))
            }

            loadPaths(*paths.toTypedArray())
        }

        registerChannel("func:load-path") {
            loadPaths(NetUtil.readUtf8(this))
        }

        onDisable.add {
            val renderEngine = UIEngine.clientApi.renderEngine()
            textures.forEach { renderEngine.deleteTexture(it) }
        }
    }

    fun loadPaths(vararg paths: String) {
        val url = paths[0].split("/").dropLast(1).joinToString("/") + "/"

        loadTextures(
            url,
            *paths.map { load(it.split("/").last(), "FUNC${it.hashCode()}FUNC") }
                .toTypedArray().apply { textures.addAll(map { it.location }) },
        ).thenAccept {
            UIEngine.clientApi.clientConnection().sendPayload("func:loaded", Unpooled.buffer())
        }
    }
}
