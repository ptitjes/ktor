// ktlint-disable filename
/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.tests.server.cio

import io.ktor.server.cio.*
import io.ktor.server.testing.*
import io.ktor.util.*

class CIOHttpServerTest : HttpServerCommonTestSuite<CIOApplicationEngine, CIOApplicationEngine.Configuration>(CIO) {
    init {
        enableHttp2 = false
        enableSsl = false
    }

    override fun testFlushingHeaders() {
        if (PlatformUtils.IS_NATIVE) return
        super.testFlushingHeaders()
    }
}