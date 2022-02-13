package socket

import PASSWORD
import app
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.util.AttributeKey
import kotlinx.coroutines.runBlocking
import me.func.protocol.packet.DataPackage
import me.func.protocol.packet.GreetingPackage
import util.UtilNetty.readFrame
import util.UtilNetty.toFrame
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

class ServerSocketHandler : SimpleChannelInboundHandler<WebSocketFrame>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: WebSocketFrame) {
        if (msg is TextWebSocketFrame) {
            val dataPackage: DataPackage = readFrame(msg)
            val channel = ctx.channel()

            if (dataPackage is GreetingPackage) {
                if (channel.hasAttr(serverInfoKey)) {
                    println("Some channel tries to authorize, but it already in system!")
                    return
                }
                if (connectedChannels.containsKey(dataPackage.serverName)) {
                    println("Channel want to register as ${dataPackage.serverName} but this name already in use!")
                    ctx.close()
                    return
                }
                if (dataPackage.password != PASSWORD) {
                    println("Channel provided bad password: ${dataPackage.password}")
                    if (channel.remoteAddress() is InetSocketAddress)
                        println(channel.remoteAddress().toString())
                    ctx.close()
                    return
                }

                channel.attr(serverInfoKey).set(dataPackage.serverName)
                connectedChannels[dataPackage.serverName!!] = channel

                println("Server authorized! ${dataPackage.serverName}")
            } else {
                if (!channel.hasAttr(serverInfoKey)) {
                    println("Some channel tries to send packet without authorization!")
                    if (channel.remoteAddress() is InetSocketAddress)
                        println(channel.remoteAddress().toString())
                    ctx.close()
                    return
                }
                val info = channel.attr(serverInfoKey).get()

                runBlocking { app.handlers[dataPackage::class.java]?.handle(channel, info, dataPackage) }
            }
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val channel = ctx.channel()

        if (channel.hasAttr(serverInfoKey)) {
            val name = channel.attr(serverInfoKey).get()
            connectedChannels.remove(name)
            println("Server disconnected! $name")
        }
    }

    companion object {
        private val serverInfoKey = AttributeKey.newInstance<String>("serverinfo")
        private val connectedChannels: MutableMap<String, Channel> = ConcurrentHashMap()

        fun broadcast(pckg: DataPackage) {
            connectedChannels.values.forEach(Consumer { channel: Channel ->
                send(channel, toFrame(pckg))
            })
        }

        fun send(channel: Channel, pckg: DataPackage) {
            send(channel, toFrame(pckg))
        }

        fun send(channel: Channel, frame: TextWebSocketFrame?) {
            channel.writeAndFlush(frame, channel.voidPromise())
        }
    }
}