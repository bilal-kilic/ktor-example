package com.bilalkilic

import com.bilalkilic.infra.userController
import com.bilalkilic.infra.modules.commandBusModule
import com.bilalkilic.infra.modules.couchbaseModule
import com.trendyol.kediatr.CommandBus
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject

@KtorExperimentalAPI
fun Application.main() {
    embeddedServer(Netty, 8085, module = Application::module, configure = {
        shareWorkGroup = true
        connectionGroupSize = parallelism / 2 + 1
        workerGroupSize = parallelism / 2 + 1
        callGroupSize = 16
    }).start(wait = true)
}

fun Application.module() {
    startKoin {
        modules(commandBusModule)
        modules(couchbaseModule)
    }

    install(Compression) {
        gzip {
            priority = 1.0
        }

        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    //install(CallLogging)
    val commandBus by inject<CommandBus>()

    routing {
        userController()

        get("/_monitoring/health/readiness") {
            // Check databases/other services.
            call.respondText("OK")
        }
        get("/_monitoring/health/liveness") {
            // Check databases/other services.
            call.respondText("OK")
        }
    }
}