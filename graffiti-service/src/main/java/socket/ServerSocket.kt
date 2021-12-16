package socket

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

class ServerSocket(var port: Int) : Thread() {

    companion object {
        private var CHANNEL_CLASS: Class<out ServerSocketChannel?>? = null
        private var BOSS_GROUP: EventLoopGroup? = null
        private var WORKER_GROUP: EventLoopGroup? = null

        init {
            var epoll = true
            try {
                Class.forName("io.netty.channel.epoll.Epoll")
                //epoll = !Boolean.getBoolean("cristalix.net.disable-native-transport") && Epoll.isAvailable();
            } catch (e: ClassNotFoundException) {
                epoll = false
            }
            if (epoll) {
                CHANNEL_CLASS = EpollServerSocketChannel::class.java
                BOSS_GROUP = EpollEventLoopGroup(1)
                WORKER_GROUP = EpollEventLoopGroup(1)
            } else {
                CHANNEL_CLASS = NioServerSocketChannel::class.java
                BOSS_GROUP = NioEventLoopGroup(1)
                WORKER_GROUP = NioEventLoopGroup(1)
            }
        }
    }

    override fun run() {
        try {
            val serverBootstrap = ServerBootstrap()
            serverBootstrap
                .group(BOSS_GROUP, WORKER_GROUP)
                .channel(CHANNEL_CLASS)
                .handler(LoggingHandler(LogLevel.INFO))
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(socketChannel: SocketChannel) {
                        socketChannel.pipeline().addLast(
                            HttpRequestDecoder(),
                            HttpObjectAggregator(65536),
                            HttpResponseEncoder(),
                            WebSocketServerProtocolHandler("/", null, false, 65536),
                            ServerSocketHandler()
                        )
                    }
                })
            serverBootstrap.bind(port).sync().channel().closeFuture().sync()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } finally {
            BOSS_GROUP!!.shutdownGracefully()
            WORKER_GROUP!!.shutdownGracefully()
        }
    }
}