package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class ExtendedReservationWithBarber(
    var reservationId: Long,
    var clientEmail: String,
    var barberEmail: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var status: String,

    var barberId: Long,
    var barbershopName: String
)