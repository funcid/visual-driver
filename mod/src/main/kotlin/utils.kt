import Main.Companion.gson
import io.netty.buffer.ByteBuf
import java.nio.charset.StandardCharsets

fun ByteBuf.readString(): String {
    val buffer = ByteArray(readableBytes())
    readBytes(buffer)
    return String(buffer, StandardCharsets.UTF_8)
}

inline fun <reified T> ByteBuf.readJson(): T {
    val data = readString()
    return gson.fromJson(data, T::class.java)
}