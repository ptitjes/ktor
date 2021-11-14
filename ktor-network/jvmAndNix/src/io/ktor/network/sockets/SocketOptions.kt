/*
* Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/

package io.ktor.network.sockets

internal const val INFINITE_TIMEOUT_MS = Long.MAX_VALUE

/**
 * Socket options builder
 */
@OptIn(ExperimentalUnsignedTypes::class)
public sealed class SocketOptions(
    @Suppress("KDocMissingDocumentation") protected val customOptions: MutableMap<Any, Any?>
) {
    /**
     * Copy options
     */
    internal abstract fun copy(): SocketOptions

    @Suppress("KDocMissingDocumentation")
    protected open fun copyCommon(from: SocketOptions) {
        typeOfService = from.typeOfService
        reuseAddress = from.reuseAddress
        reusePort = from.reusePort
    }

    internal fun peer(): PeerSocketOptions {
        return PeerSocketOptions(HashMap(customOptions)).apply {
            copyCommon(this@SocketOptions)
        }
    }

    internal fun streamServer(): StreamServerSocketOptions {
        return StreamServerSocketOptions(HashMap(customOptions)).apply {
            copyCommon(this@SocketOptions)
        }
    }

    private class GeneralSocketOptions constructor(
        customOptions: MutableMap<Any, Any?>
    ) : SocketOptions(customOptions) {
        override fun copy(): GeneralSocketOptions = GeneralSocketOptions(HashMap(customOptions)).apply {
            copyCommon(this@GeneralSocketOptions)
        }
    }

    /**
     * ToS value, [TypeOfService.UNDEFINED] by default, may not work with old JDK (will be silently ignored)
     */
    public var typeOfService: TypeOfService = TypeOfService.UNDEFINED

    /**
     * SO_REUSEADDR option
     */
    public var reuseAddress: Boolean = false

    /**
     * SO_REUSEPORT option, may not work with old JDK (will be silently ignored)
     */
    public var reusePort: Boolean = false

    /**
     * Stream server socket options
     */
    public class StreamServerSocketOptions internal constructor(
        customOptions: MutableMap<Any, Any?>
    ) : SocketOptions(customOptions) {
        /**
         * Represents TCP server socket backlog size. When a client attempts to connect,
         * the request is added to the so called backlog until it will be accepted.
         * Once accept() is invoked, a client socket is removed from the backlog.
         * If the backlog is too small, it may overflow and upcoming requests will be
         * rejected by the underlying TCP implementation (usually with RST frame that
         * usually causes "connection reset by peer" error on the opposite side).
         */
        public var backlogSize: Int = 511

        override fun copy(): StreamServerSocketOptions {
            return StreamServerSocketOptions(HashMap(customOptions)).apply {
                copyCommon(this@StreamServerSocketOptions)
            }
        }
    }

    /**
     * Represents stream client or datagram socket options
     */
    public open class PeerSocketOptions internal constructor(
        customOptions: MutableMap<Any, Any?>
    ) : SocketOptions(customOptions) {

        /**
         * Socket ougoing buffer size (SO_SNDBUF), `-1` or `0` to make system decide
         */
        public var sendBufferSize: Int = -1

        /**
         * Socket incoming buffer size (SO_RCVBUF), `-1` or `0` to make system decide
         */
        public var receiveBufferSize: Int = -1

        @Suppress("KDocMissingDocumentation")
        override fun copyCommon(from: SocketOptions) {
            super.copyCommon(from)
            if (from is PeerSocketOptions) {
                sendBufferSize = from.sendBufferSize
                receiveBufferSize = from.receiveBufferSize
            }
        }

        override fun copy(): PeerSocketOptions {
            return PeerSocketOptions(HashMap(customOptions)).apply {
                copyCommon(this@PeerSocketOptions)
            }
        }

        internal fun streamClient(): StreamClientSocketOptions {
            return StreamClientSocketOptions(HashMap(customOptions)).apply {
                copyCommon(this@PeerSocketOptions)
            }
        }

        internal fun datagram(): DatagramSocketOptions {
            return DatagramSocketOptions(HashMap(customOptions)).apply {
                copyCommon(this@PeerSocketOptions)
            }
        }
    }

    /**
     * Represents UDP socket options
     */
    public class DatagramSocketOptions internal constructor(
        customOptions: MutableMap<Any, Any?>
    ) : PeerSocketOptions(customOptions) {

        /**
         * SO_BROADCAST socket option
         */
        public var broadcast: Boolean = false

        override fun copyCommon(from: SocketOptions) {
            super.copyCommon(from)
            if (from is DatagramSocketOptions) {
                broadcast = from.broadcast
            }
        }

        override fun copy(): DatagramSocketOptions {
            return DatagramSocketOptions(HashMap(customOptions)).apply {
                copyCommon(this@DatagramSocketOptions)
            }
        }
    }

    /**
     * Represents stream client socket options
     */
    public class StreamClientSocketOptions internal constructor(
        customOptions: MutableMap<Any, Any?>
    ) : PeerSocketOptions(customOptions) {
        /**
         * TCP_NODELAY socket option, useful to disable Nagle
         */
        public var noDelay: Boolean = true

        /**
         * SO_LINGER option applied at socket close, not recommended to set to 0 however useful for debugging
         * Value of `-1` is the default and means that it is not set and system-dependant
         */
        public var lingerSeconds: Int = -1

        /**
         * SO_KEEPALIVE option is to enable/disable TCP keep-alive
         */
        public var keepAlive: Boolean? = null

        /**
         * Socket timeout (read and write).
         */
        public var socketTimeout: Long = INFINITE_TIMEOUT_MS

        @Suppress("KDocMissingDocumentation")
        override fun copyCommon(from: SocketOptions) {
            super.copyCommon(from)
            if (from is StreamClientSocketOptions) {
                noDelay = from.noDelay
                lingerSeconds = from.lingerSeconds
                keepAlive = from.keepAlive
            }
        }

        override fun copy(): StreamClientSocketOptions {
            return StreamClientSocketOptions(HashMap(customOptions)).apply {
                copyCommon(this@StreamClientSocketOptions)
            }
        }
    }

    public companion object {
        internal fun create(): SocketOptions = GeneralSocketOptions(HashMap())
    }
}
