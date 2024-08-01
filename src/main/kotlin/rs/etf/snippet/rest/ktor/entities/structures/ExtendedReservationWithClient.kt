package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class ExtendedReservationWithClient(
    var reservationId: Long,
    var clientEmail: String,
    var barberEmail: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var status: String,

    var clientId: Long,
    var clientName: String,
    var clientSurname: String
)