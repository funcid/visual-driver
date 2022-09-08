import com.google.gson.Gson
import io.netty.buffer.ByteBuf
import java.nio.charset.StandardCharsets

val gson = Gson()

fun ByteBuf.readString(): String {
    val buffer = ByteArray(readableBytes())
    readBytes(buffer)
    return String(buffer, StandardCharsets.UTF_8)
}

inline fun <reified T> ByteBuf.readJson() = gson.fromJson(readString(), T::class.java)
