package me.func.mod.graffiti

import com.google.common.cache.CacheBuilder
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.*
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.websocketx.*
import me.func.mod.util.UtilNetty
import me.func.mod.util.UtilNetty.toFrame
import me.func.protocol.graffiti.UserGraffitiData
import me.func.protocol.graffiti.packet.GraffitiBuyPackage
import me.func.protocol.graffiti.packet.GraffitiLoadUserPackage
import me.func.protocol.graffiti.packet.GraffitiUsePackage
import me.func.protocol.packet.DataPackage
import me.func.protocol.packet.GreetingPackage
import ru.cristalix.core.realm.IRealmService
import java.net.URI
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@ChannelHandler.Sharable
class DefaultGraffitiClient(val host: String, val password: String, val port: Int, val serverClient: String) : GraffitiClient, SimpleChannelInboundHandler<WebSocketFrame>() {

    override fun loadUser(uuid: UUID): CompletableFuture<UserGraffitiData?> {
        val future = CompletableFuture<UserGraffitiData?>()
        writeAndAwaitResponse(GraffitiLoadUserPackage(uuid)).exceptionally { null }.thenAcceptAsync {
            future.complete(it.data)
        }
        return future
    }

    override fun use(uuid: UUID, pack: UUID, graffiti: UUID): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        writeAndAwaitResponse(GraffitiUsePackage(uuid, pack, graffiti)).exceptionally { null }.thenAcceptAsync {
            future.complete(it.boolean)
        }
        return future
    }

    override fun buy(uuid: UUID, pack: UUID, price: Int): CompletableFuture<String?> {
        val future = CompletableFuture<String?>()
        writeAndAwaitResponse(GraffitiBuyPackage(uuid, pack, price)).exceptionally { null }.thenAcceptAsync {
            future.complete(it.errorMessage)
        }
        return future
    }

    private var CHANNEL_CLASS: Class<out SocketChannel?>? = null
    private var GROUP: EventLoopGroup? = null

    init {
        val epoll: Boolean = try {
            Class.forName("io.netty.channel.epoll.Epoll")
            !java.lang.Boolean.getBoolean("cristalix.net.disable-native-transport") && Epoll.isAvailable()
        } catch (ignored: ClassNotFoundException) {
            false
        }
        if (epoll) {
            CHANNEL_CLASS = EpollSocketChannel::class.java
            GROUP = EpollEventLoopGroup(1)
        } else {
            CHANNEL_CLASS = NioSocketChannel::class.java
            GROUP = NioEventLoopGroup(1)
        }
    }

    private val responseCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.SECONDS)
            .build<String, CompletableFuture<in DataPackage>>()
    private val handlersMap: MutableMap<Class<out DataPackage>, Consumer<in DataPackage>> = mutableMapOf()
    private var channel: Channel? = null

    override fun connect(): GraffitiClient {
        Bootstrap()
            .channel(CHANNEL_CLASS)
            .group(GROUP)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    val config = ch.config()
                    config.setOption(ChannelOption.IP_TOS, 24)
                    config.allocator = PooledByteBufAllocator.DEFAULT
                    config.setOption(ChannelOption.TCP_NODELAY, java.lang.Boolean.TRUE)
                    config.setOption(ChannelOption.SO_KEEPALIVE, java.lang.Boolean.TRUE)
                    ch.pipeline()
                        .addLast(HttpClientCodec())
                        .addLast(HttpObjectAggregator(65536))
                        .addLast(
                            WebSocketClientProtocolHandler(
                                WebSocketClientHandshakerFactory.newHandshaker(
                                    URI.create("https://$host:$port/"),
                                    WebSocketVersion.V13,
                                    null,
                                    false,
                                    DefaultHttpHeaders(),
                                    65536
                                ), true
                            )
                        ).addLast(this@DefaultGraffitiClient)
                }
            }).remoteAddress(host, port)
            .connect()
            .addListener(ChannelFutureListener { future: ChannelFuture ->
                if (future.isSuccess) {
                    println("Connection succeeded, bound to: " + future.channel().also { channel = it })
                } else {
                    println("Connection failed")
                    future.cause().printStackTrace()
                    processAutoReconnect()
                }
            })
        return this
    }

    fun <T : DataPackage> writeAndAwaitResponse(pckg: T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        responseCache.put(pckg.id, future as CompletableFuture<in DataPackage>)
        channel!!.writeAndFlush(toFrame(pckg), channel!!.voidPromise())
        return future
    }

    private fun processAutoReconnect() {
        println("Automatically reconnecting in next 1.5 seconds")
        GROUP!!.schedule({ connect() }, 1500L, TimeUnit.MILLISECONDS)
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt === WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            val greetingPackage = GreetingPackage(password, serverClient)
            channel!!.writeAndFlush(toFrame(greetingPackage)).addListeners(
                ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE,
                ChannelFutureListener { future ->
                    if (future.isSuccess) {
                        println("Handshake completed!")
                    } else {
                        println("Error during handshake")
                        future.cause().printStackTrace()
                        future.channel().close()
                    }
                })
        }
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: WebSocketFrame) {
        if (msg is TextWebSocketFrame) {
            val pckg = UtilNetty.readFrame(msg)
            val future = responseCache.getIfPresent(pckg.id)
            if (future != null) {
                responseCache.invalidate(pckg.id)
                future.complete(pckg)
            }
            val consumer = handlersMap[pckg.javaClass]
            consumer?.accept(pckg)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        channel!!.close()
        channel = null
        responseCache.invalidateAll()
        processAutoReconnect()
    }
}