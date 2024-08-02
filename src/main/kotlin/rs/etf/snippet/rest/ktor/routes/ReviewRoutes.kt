package rs.etf.snippet.rest.ktor.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rs.etf.snippet.rest.ktor.daos.ReviewDao
import rs.etf.snippet.rest.ktor.entities.tables.Review

fun Route.reviewRouting() {
    route("review") {
        post("submitReview") {
            val review = call.receive<Review>()
            ReviewDao.submitReview(review)
            call.respondText(
                "New review was successfully added to the database.",
                status = HttpStatusCode.OK
            )
        }

        get("getClientReviewsForBarber") {
            val clientEmail = call.request.queryParameters["clientEmail"]!!
            val barberEmail = call.request.queryParameters["barberEmail"]!!
            val reviews = ReviewDao.getClientReviewsForBarber(clientEmail, barberEmail)
            call.respond(reviews)
        }

        get("getBarberReviews") {
            val barberEmail = call.request.queryParameters["barberEmail"]!!
            val reviews = ReviewDao.getBarberReviews(barberEmail)
            call.respond(reviews)
        }

        get("getClientReviews") {
            val clientEmail = call.request.queryParameters["clientEmail"]!!
            val reviews = ReviewDao.getClientReviews(clientEmail)
            call.respond(reviews)
        }

        get("getBarberAverageGrade") {
            val barberEmail = call.request.queryParameters["barberEmail"]!!
            val averageGrade = ReviewDao.getBarberAverageGrade(barberEmail)
            call.respond(averageGrade)
        }
    }
}