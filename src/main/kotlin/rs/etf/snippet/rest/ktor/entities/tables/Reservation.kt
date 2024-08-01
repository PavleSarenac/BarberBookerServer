package rs.etf.snippet.rest.ktor.entities.tables

import kotlinx.serialization.Serializable

@Serializable
data class Reservation(
    val id: Long = 0,
    var clientEmail: String,
    var barberEmail: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var status: String
)