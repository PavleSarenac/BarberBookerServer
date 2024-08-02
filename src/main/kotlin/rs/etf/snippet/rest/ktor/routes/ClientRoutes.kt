package rs.etf.snippet.rest.ktor.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rs.etf.snippet.rest.ktor.daos.ClientDao
import rs.etf.snippet.rest.ktor.entities.tables.Client

fun Route.clientRouting() {
    route("client") {
        post("addNewClient") {
            val newClient = call.receive<Client>()
            ClientDao.addNewClient(newClient)
            call.respondText(
                "New client was successfully added to the database.",
                status = HttpStatusCode.OK
            )
        }

        get("getClientByEmail") {
            val clientEmail = call.request.queryParameters["email"]!!
            val client = ClientDao.getClientByEmail(clientEmail)
            if (client == null) {
                call.respondText("Client not found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(client)
            }
        }

        get("getClientByEmailAndPassword") {
            val clientEmail = call.request.queryParameters["email"]!!
            val clientPassword = call.request.queryParameters["password"]!!
            val client = ClientDao.getClientByEmailAndPassword(clientEmail, clientPassword)
            if (client == null) {
                call.respondText("Client not found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(client)
            }
        }

        get("updateClientProfile") {
            val email = call.request.queryParameters["email"]!!
            val name = call.request.queryParameters["name"]!!
            val surname = call.request.queryParameters["surname"]!!
            ClientDao.updateClientProfile(email, name, surname)
            call.respondText(
                "Client profile successfully updated.",
                status = HttpStatusCode.OK
            )
        }
    }
}