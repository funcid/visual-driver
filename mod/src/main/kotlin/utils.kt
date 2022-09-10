import com.google.gson.Gson
import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import java.nio.charset.StandardCharsets

val gson = Gson()

fun ByteBuf.readFullAsString(): String {
    val buffer = ByteArray(readableBytes())
    readBytes(buffer)
    return String(buffer, StandardCharsets.UTF_8)
}

inline fun <reified T> ByteBuf.readJson(): T = gson.fromJson(readFullAsString(), T::class.java)

fun ByteBuf.readColoredUtf8() = NetUtil.readUtf8(this).replace("&", "§")
