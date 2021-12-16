import io.netty.channel.Channel
import me.func.protocol.packet.DataPackage

fun interface PackageHandler<T : DataPackage>  {

    fun handle(channel: Channel, serverName: String, dataPackage: T)

}
