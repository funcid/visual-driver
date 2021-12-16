package util

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import me.func.protocol.packet.DataPackage
import me.func.protocol.packet.PackageWrapper
import ru.cristalix.core.GlobalSerializers

object UtilNetty {

    @JvmStatic
    fun toFrame(dataPackage: DataPackage): TextWebSocketFrame {
        return TextWebSocketFrame(
            GlobalSerializers.toJson(
                PackageWrapper(
                    dataPackage::class.java.name,
                    GlobalSerializers.toJson(dataPackage)
                )
            )
        )
    }

    @JvmStatic
    fun readFrame(textFrame: TextWebSocketFrame): DataPackage {
        val wrapper: PackageWrapper = GlobalSerializers.fromJson(textFrame.text(), PackageWrapper::class.java)
        return GlobalSerializers.fromJson(wrapper.objectData, Class.forName(wrapper.clazz)) as DataPackage
    }
}
