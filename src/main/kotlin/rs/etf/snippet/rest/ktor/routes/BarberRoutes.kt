package rs.etf.snippet.rest.ktor.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rs.etf.snippet.rest.ktor.daos.BarberDao
import rs.etf.snippet.rest.ktor.entities.structures.FcmTokenUpdateData
import rs.etf.snippet.rest.ktor.entities.tables.Barber

fun Route.barberRouting() {
    route("barber") {
        post("addNewBarber") {
            val newBarber = call.receive<Barber>()
            BarberDao.addNewBarber(newBarber)
            call.respondText(
                "New barber was successfully added to the database.",
                status = HttpStatusCode.OK
            )
        }

        get("getBarberByEmail") {
            val barberEmail = call.request.queryParameters["email"]!!
            val barber = BarberDao.getBarberByEmail(barberEmail)
            if (barber == null) {
                call.respondText("Barber not found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(barber)
            }
        }

        get("getBarberByEmailAndPassword") {
            val barberEmail = call.request.queryParameters["email"]!!
            val barberPassword = call.request.queryParameters["password"]!!
            val barber = BarberDao.getBarberByEmailAndPassword(barberEmail, barberPassword)
            if (barber == null) {
                call.respondText("Barber not found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(barber)
            }
        }

        get("updateBarberProfile") {
            val email = call.request.queryParameters["email"]!!
            val barbershopName = call.request.queryParameters["barbershopName"]!!
            val price = call.request.queryParameters["price"]!!.toDouble()
            val phone = call.request.queryParameters["phone"]!!
            val country = call.request.queryParameters["country"]!!
            val city = call.request.queryParameters["city"]!!
            val municipality = call.request.queryParameters["municipality"]!!
            val address = call.request.queryParameters["address"]!!
            val workingDays = call.request.queryParameters["workingDays"]!!
            val workingHours = call.request.queryParameters["workingHours"]!!

            BarberDao.updateBarberProfile(
                email,
                barbershopName,
                price,
                phone,
                country,
                city,
                municipality,
                address,
                workingDays,
                workingHours
            )

            call.respondText(
                "Barber profile successfully updated.",
                status = HttpStatusCode.OK
            )
        }

        get("getSearchResults") {
            val query = call.request.queryParameters["query"]!!
            val searchResults = BarberDao.getSearchResults(query)
            call.respond(searchResults)
        }

        post("updateFcmToken") {
            val fcmTokenUpdateData = call.receive<FcmTokenUpdateData>()
            BarberDao.updateFcmToken(
                email = fcmTokenUpdateData.email,
                fcmToken = fcmTokenUpdateData.fcmToken
            )
            call.respondText(
                "Fcm token was successfully updated.",
                status = HttpStatusCode.OK
            )
        }
    }
}