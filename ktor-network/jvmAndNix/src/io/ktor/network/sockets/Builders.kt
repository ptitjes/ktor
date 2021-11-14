/*
* Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/
package io.ktor.network.sockets

import io.ktor.network.selector.*

/**
 * Start building a socket
 */
public fun aSocket(selector: SelectorManager): SocketBuilder = SocketBuilder(selector, SocketOptions.create())

/**
 * Socket builder
 */
@Suppress("PublicApiImplicitType", "unused")
public class SocketBuilder internal constructor(
    private val selector: SelectorManager,
    override var options: SocketOptions
) : Configurable<SocketBuilder, SocketOptions> {

    /**
     * Build a stream socket (TCP or unix domain stream socket).
     */
    public fun stream(): StreamSocketBuilder = StreamSocketBuilder(selector, options.peer())

    /**
     * Build a datagram socket (UDP or unix domain datagram socket).
     */
    public fun datagram(): DatagramSocketBuilder = DatagramSocketBuilder(selector, options.peer().datagram())

    /**
     * Build TCP socket.
     */
    @Deprecated(
        "tcp() has been replaced with stream().",
        ReplaceWith("stream()"),
        level = DeprecationLevel.WARNING
    )
    public fun tcp(): StreamSocketBuilder = StreamSocketBuilder(selector, options.peer())

    /**
     * Build UDP socket.
     */
    @Deprecated(
        "udp() has been replaced with datagram().",
        ReplaceWith("datagram()"),
        level = DeprecationLevel.WARNING
    )
    public fun udp(): DatagramSocketBuilder = DatagramSocketBuilder(selector, options.peer().datagram())
}

/**
 * Set TCP_NODELAY socket option to disable the Nagle algorithm.
 */
public fun <T : Configurable<T, *>> T.tcpNoDelay(): T {
    return configure {
        if (this is SocketOptions.StreamClientSocketOptions) {
            noDelay = true
        }
    }
}

/**
 * Represent a configurable socket
 */
public interface Configurable<out T : Configurable<T, Options>, Options : SocketOptions> {
    /**
     * Current socket options
     */
    public var options: Options

    /**
     * Configure socket options in [block] function
     */
    public fun configure(block: Options.() -> Unit): T {
        @Suppress("UNCHECKED_CAST")
        val newOptions = options.copy() as Options

        block(newOptions)
        options = newOptions

        @Suppress("UNCHECKED_CAST")
        return this as T
    }
}
