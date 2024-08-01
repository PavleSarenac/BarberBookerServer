package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class ExtendedReviewWithBarber(
    var reviewId: Long,
    var clientEmail: String,
    var barberEmail: String,
    var grade: Int,
    var text: String,
    var date: String,

    var barberId: Long,
    var barbershopName: String
)