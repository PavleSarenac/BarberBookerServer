package rs.etf.snippet.rest.ktor.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rs.etf.snippet.rest.ktor.daos.ReservationDao
import rs.etf.snippet.rest.ktor.entities.tables.Reservation

fun Route.reservationRouting() {
    route("reservation") {
        post("addNewReservation") {
            val reservation = call.receive<Reservation>()
            ReservationDao.addNewReservation(reservation)
            call.respondText(
                "New reservation was successfully added to the database.",
                status = HttpStatusCode.OK
            )
        }

        get("getBarberReservationByDateTime") {
            val barberEmail = call.request.queryParameters["barberEmail"]!!
            val date = call.request.queryParameters["date"]!!
            val time = call.request.queryParameters["time"]!!
            val reservation = ReservationDao.getBarberReservationByDateTime(barberEmail, date, time)
            if (reservation == null) {
                call.respondText("Reservation not found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(reservation)
            }
        }

        get("getClientReservationByDateTime") {
            val clientEmail = call.request.queryParameters["clientEmail"]!!
            val date = call.request.queryParameters["date"]!!
            val time = call.request.queryParameters["time"]!!
            val reservation = ReservationDao.getClientReservationByDateTime(clientEmail, date, time)
            if (reservation == null) {
                call.respondText("Reservation not found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(reservation)
            }
        }

        get("updateReservationStatuses") {
            val currentDate = call.request.queryParameters["currentDate"]!!
            val currentTime = call.request.queryParameters["currentTime"]!!
            ReservationDao.updateReservationStatuses(currentDate, currentTime)
            call.respondText(
                "Reservation statuses updated.",
                status = HttpStatusCode.OK
            )
        }

        get("updatePendingRequests") {
            val currentDate = call.request.queryParameters["currentDate"]!!
            val currentTime = call.request.queryParameters["currentTime"]!!
            ReservationDao.updatePendingRequests(currentDate, currentTime)
            call.respondText(
                "Reservation statuses updated.",
                status = HttpStatusCode.OK
            )
        }

        get("getRejectedReservationRequest") {
            val clientEmail = call.request.queryParameters["clientEmail"]!!
            val barberEmail = call.request.queryParameters["barberEmail"]!!
            val date = call.request.queryParameters["date"]!!
            val time = call.request.queryParameters["time"]!!
            val request = ReservationDao.getRejectedReservationRequest(clientEmail, barberEmail, date, time)
            if (request == null) {
                call.respondText("Request not found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(request)
            }
        }

        get("getClientPendingReservationRequests") {
            val clientEmail = call.request.queryParameters["clientEmail"]!!
            val requests = ReservationDao.getClientPendingReservationRequests(clientEmail)
            call.respond(requests)
        }

        get("getClientAppointments") {
            val clientEmail = call.request.queryParameters["clientEmail"]!!
            val requests = ReservationDao.getClientAppointments(clientEmail)
            call.respond(requests)
        }

        get("getClientRejections") {
            val clientEmail = call.request.queryParameters["clientEmail"]!!
            val requests = ReservationDao.getClientRejections(clientEmail)
            call.respond(requests)
        }

        get("getClientArchive") {
            val clientEmail = call.request.queryParameters["clientEmail"]!!
            val requests = ReservationDao.getClientArchive(clientEmail)
            call.respond(requests)
        }

        get("getBarberPendingReservationRequests") {
            val barberEmail = call.request.queryParameters["barberEmail"]!!
            val requests = ReservationDao.getBarberPendingReservationRequests(barberEmail)
            call.respond(requests)
        }

        get("getBarberAppointments") {
            val barberEmail = call.request.queryParameters["barberEmail"]!!
            val requests = ReservationDao.getBarberAppointments(barberEmail)
            call.respond(requests)
        }

        get("getBarberArchive") {
            val barberEmail = call.request.queryParameters["barberEmail"]!!
            val requests = ReservationDao.getBarberArchive(barberEmail)
            call.respond(requests)
        }

        get("getBarberRejections") {
            val barberEmail = call.request.queryParameters["barberEmail"]!!
            val requests = ReservationDao.getBarberRejections(barberEmail)
            call.respond(requests)
        }

        get("getBarberConfirmations") {
            val barberEmail = call.request.queryParameters["barberEmail"]!!
            val requests = ReservationDao.getBarberConfirmations(barberEmail)
            call.respond(requests)
        }

        get("acceptReservationRequest") {
            val reservationId = call.request.queryParameters["reservationId"]!!.toInt()
            ReservationDao.acceptReservationRequest(reservationId)
            call.respondText(
                "Reservation request was successfully accepted.",
                status = HttpStatusCode.OK
            )
        }

        get("rejectReservationRequest") {
            val reservationId = call.request.queryParameters["reservationId"]!!.toInt()
            ReservationDao.rejectReservationRequest(reservationId)
            call.respondText(
                "Reservation request was successfully rejected.",
                status = HttpStatusCode.OK
            )
        }

        get("updateDoneReservationStatus") {
            val reservationId = call.request.queryParameters["reservationId"]!!.toInt()
            val status = call.request.queryParameters["status"]!!
            ReservationDao.updateDoneReservationStatus(reservationId, status)
            call.respondText(
                "Done reservation status was successfully updated.",
                status = HttpStatusCode.OK
            )
        }
    }
}