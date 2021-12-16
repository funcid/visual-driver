import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.UIEngine

object ExternalManager {

    init {
        App::class.mod.registerChannel("func:load-paths") {
            val count = readInt()
            val paths = mutableListOf<String>()

            repeat(count) {
                paths.add(NetUtil.readUtf8(this))
            }

            loadPaths(*paths.toTypedArray())
        }

        App::class.mod.registerChannel("func:load-path") {
            loadPaths(NetUtil.readUtf8(this))
        }
    }

    private fun loadPaths(vararg paths: String) {
        val url = paths[0].split("/").dropLast(1).joinToString("/") + "/"

        loadTextures(
            url,
            *paths.map { load(it.split("/").last(), "FUNC${it.hashCode()}FUNC") }
                .toTypedArray(),
        ).thenAccept {
            UIEngine.clientApi.clientConnection().sendPayload("func:loaded", Unpooled.buffer())
        }
    }

}