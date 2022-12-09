import com.google.gson.Gson
import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import me.func.protocol.data.color.RGB
import me.func.protocol.data.color.Tricolor
import model.LazyElement
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.Color
import java.nio.charset.StandardCharsets
import java.util.*

val gson = Gson()

fun ByteBuf.readFullAsString(): String {
    val buffer = ByteArray(readableBytes())
    readBytes(buffer)
    return String(buffer, StandardCharsets.UTF_8)
}

inline fun <reified T> ByteBuf.readJson(): T = gson.fromJson(readFullAsString(), T::class.java)

fun ByteBuf.readColoredUtf8() = NetUtil.readUtf8(this).replace("&", "§")

fun ByteBuf.readRgb() = Tricolor(readInt(), readInt(), readInt())

fun ByteBuf.readUuid(): UUID = UUID(readLong(), readLong())

fun ByteBuf.writeUuid(uuid: UUID) = ensureWritable(16)
    .writeLong(uuid.mostSignificantBits)
    .writeLong(uuid.leastSignificantBits)

fun RGB.asColor(opacity: Double = 1.0) = Color(red, green, blue, opacity)

fun lazyRectangle(element: RectangleElement.() -> Unit) = LazyElement { RectangleElement().also(element) }

fun lazyText(element: TextElement.() -> Unit) = LazyElement { TextElement().also(element) }

fun lazyCarved(element: CarvedRectangle.() -> Unit) = LazyElement { CarvedRectangle().also(element) }

fun formatPriceText(sale: Int, price: Long, priceText: String): String =
    if (sale > 0) "§7§m$priceText§a ${(price * (100.0 - sale) / 100).toInt()} §c§l-$sale%" else priceText