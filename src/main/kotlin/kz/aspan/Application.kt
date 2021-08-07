package kz.aspan

import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.Routing
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kz.aspan.data.checkPasswordForEmail
import kz.aspan.routes.loginRoute
import kz.aspan.routes.noteRoutes
import kz.aspan.routes.registerRoute

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(DefaultHeaders)
        install(CallLogging)
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        install(Authentication) {
            configureAuth()
        }
        install(Routing) {
            registerRoute()
            loginRoute()
            noteRoutes()
        }
    }.start(wait = true)
}


private fun Authentication.Configuration.configureAuth() {
    basic {
        realm = "Note Server"
        validate { credentials ->
            val email = credentials.name
            val password = credentials.password
            if (checkPasswordForEmail(email, password)) {
                UserIdPrincipal(email)
            } else null
        }
    }
}
