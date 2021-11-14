package io.ktor.network.sockets

import io.ktor.network.selector.*

/**
 * Stream socket builder (TCP or unix domain stream socket)
 */
@Suppress("PublicApiImplicitType")
public class StreamSocketBuilder(
    private val selector: SelectorManager,
    override var options: SocketOptions
) : Configurable<StreamSocketBuilder, SocketOptions> {
    /**
     * Connect to [hostname] and [port].
     */
    public suspend fun connect(
        hostname: String,
        port: Int,
        configure: SocketOptions.StreamClientSocketOptions.() -> Unit = {}
    ): Socket = connect(InetSocketAddress(hostname, port), configure)

    /**
     * Bind server socket at [port] to listen to [hostname].
     */
    public fun bind(
        hostname: String = "0.0.0.0",
        port: Int = 0,
        configure: SocketOptions.StreamServerSocketOptions.() -> Unit = {}
    ): ServerSocket = bind(InetSocketAddress(hostname, port), configure)

    /**
     * Connect to [remoteAddress].
     */
    public suspend fun connect(
        remoteAddress: SocketAddress,
        configure: SocketOptions.StreamClientSocketOptions.() -> Unit = {}
    ): Socket = connect(selector, remoteAddress, options.peer().streamClient().apply(configure))

    /**
     * Bind server socket to listen to [localAddress].
     */
    public fun bind(
        localAddress: SocketAddress? = null,
        configure: SocketOptions.StreamServerSocketOptions.() -> Unit = {}
    ): ServerSocket = bind(selector, localAddress, options.peer().streamServer().apply(configure))
}
