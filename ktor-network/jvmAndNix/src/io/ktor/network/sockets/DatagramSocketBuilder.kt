package io.ktor.network.sockets

import io.ktor.network.selector.*

/**
 * Datagram socket builder (UDP or unix domain datagram socket)
 */
public class DatagramSocketBuilder(
    private val selector: SelectorManager,
    override var options: SocketOptions.DatagramSocketOptions
) : Configurable<DatagramSocketBuilder, SocketOptions.DatagramSocketOptions> {
    /**
     * Bind server socket to listen to [localAddress].
     */
    public fun bind(
        localAddress: SocketAddress? = null,
        configure: SocketOptions.DatagramSocketOptions.() -> Unit = {}
    ): BoundDatagramSocket = bindUDP(selector, localAddress, options.datagram().apply(configure))

    /**
     * Create a datagram socket to listen datagrams at [localAddress] and set to [remoteAddress].
     */
    public fun connect(
        remoteAddress: SocketAddress,
        localAddress: SocketAddress? = null,
        configure: SocketOptions.DatagramSocketOptions.() -> Unit = {}
    ): ConnectedDatagramSocket = connectUDP(selector, remoteAddress, localAddress, options.datagram().apply(configure))

    public companion object
}

internal expect fun DatagramSocketBuilder.Companion.connectUDP(
    selector: SelectorManager,
    remoteAddress: SocketAddress,
    localAddress: SocketAddress?,
    options: SocketOptions.DatagramSocketOptions
): ConnectedDatagramSocket

internal expect fun DatagramSocketBuilder.Companion.bindUDP(
    selector: SelectorManager,
    localAddress: SocketAddress?,
    options: SocketOptions.DatagramSocketOptions
): BoundDatagramSocket
