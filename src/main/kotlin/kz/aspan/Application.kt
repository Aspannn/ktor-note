package kz.aspan

import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.Routing
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.aspan.data.collections.User
import kz.aspan.data.registerUser
import kz.aspan.routes.registerRoute

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(DefaultHeaders)
        install(CallLogging)
        install(Routing) {
            registerRoute()
        }
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }

    }.start(wait = true)
}
