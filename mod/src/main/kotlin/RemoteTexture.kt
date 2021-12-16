import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.UIEngine
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

data class RemoteTexture(val location: ResourceLocation, val sha1: String)

private val cacheDir = Paths.get("$NAMESPACE/")

fun loadTextures(urlString: String, vararg info: RemoteTexture): CompletableFuture<Nothing> {
    val future = CompletableFuture<Nothing>()
    CompletableFuture.runAsync {
        for (photo in info) {
            try {
                val cacheDir = cacheDir
                Files.createDirectories(cacheDir)
                val path = cacheDir.resolve(photo.sha1)

                val image = try {
                    Files.newInputStream(path).use {
                        ImageIO.read(it)
                    }
                } catch (ex: IOException) {
                    val url = URL("$urlString${photo.location.path}")
                    val bytes = url.openStream().readBytes()
                    Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                    ImageIO.read(ByteArrayInputStream(bytes))
                }
                val api = UIEngine.clientApi
                val mc = api.minecraft()
                val renderEngine = api.renderEngine()
                mc.execute {
                    renderEngine.loadTexture(photo.location, renderEngine.newImageTexture(image, false, false))
                    future.complete(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                future.completeExceptionally(e)
            }
        }
    }
    return future
}

fun load(path: String, hash: String): RemoteTexture {
    return RemoteTexture(ResourceLocation.of(NAMESPACE, path), hash)
}