package rs.etf.snippet.rest.ktor.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import rs.etf.snippet.rest.ktor.routes.barberRouting
import rs.etf.snippet.rest.ktor.routes.clientRouting
import rs.etf.snippet.rest.ktor.routes.reservationRouting
import rs.etf.snippet.rest.ktor.routes.reviewRouting

fun Application.configureRouting() {
    routing {
        barberRouting()
        clientRouting()
        reservationRouting()
        reviewRouting()
    }
}
