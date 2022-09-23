package standard.util

import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import ru.cristalix.clientapi.JavaMod.loadTextureFromJar
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine

class ExternalManager {
    private val textures = arrayListOf<ResourceLocation>()

    init {
        mod.registerChannel("func:load-paths") {
            val count = readInt()
            val paths = arrayListOf<String>()

            repeat(count) {
                paths.add(NetUtil.readUtf8(this))
            }

            loadPaths(*paths.toTypedArray())
        }

        mod.registerChannel("func:load-path") {
            loadPaths(NetUtil.readUtf8(this))
        }

        mod.onDisable.add {
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
            UIEngine.clientApi.clientConnection().sendPayload("func:loaded", Unpooled.EMPTY_BUFFER)
        }
    }

    fun load(path: String): ResourceLocation {
        val parts = path.split(":", limit = 2)
        val last = parts.last()
        val location = when {
            path.startsWith("runtime") -> loadTextureFromJar(UIEngine.clientApi, "icons", last, "$last.png")
            path.startsWith("download") -> ResourceLocation.of("cache/animation", "$last.png").apply {
                loadPaths("https://storage.c7x.dev/func/animation-api/$last.png")
            }
            else -> ResourceLocation.of(parts.first(), last)
        }
        textures += location
        return location
    }
}
