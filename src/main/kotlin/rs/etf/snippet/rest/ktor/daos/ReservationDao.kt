package rs.etf.snippet.rest.ktor.daos

import rs.etf.snippet.rest.ktor.DatabaseFactory
import rs.etf.snippet.rest.ktor.entities.structures.ExtendedReservationWithBarber
import rs.etf.snippet.rest.ktor.entities.structures.ExtendedReservationWithClient
import rs.etf.snippet.rest.ktor.entities.tables.Reservation
import java.sql.Connection

object ReservationDao {

    fun addNewReservation(reservation: Reservation) {
        var connection: Connection? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    INSERT INTO reservation (
                        clientEmail,
                        barberEmail,
                        date,
                        startTime,
                        endTime,
                        status
                    )
                    VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent()
            )
            statement.setString(1, reservation.clientEmail)
            statement.setString(2, reservation.barberEmail)
            statement.setString(3, reservation.date)
            statement.setString(4, reservation.startTime)
            statement.setString(5, reservation.endTime)
            statement.setString(6, reservation.status)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun updateReservationStatuses(
        currentDate: String,
        currentTime: String
    ) {
        var connection: Connection? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE reservation 
                    SET status = 'WAITING_CONFIRMATION'
                    WHERE date = ? AND ? >= endTime AND status = 'ACCEPTED'
                """.trimIndent()
            )
            statement.setString(1, currentDate)
            statement.setString(2, currentTime)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun updatePendingRequests(
        currentDate: String,
        currentTime: String
    ) {
        var connection: Connection? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE reservation 
                    SET status = 'REJECTED'
                    WHERE date = ? AND ? >= endTime AND status = 'PENDING'
                """.trimIndent()
            )
            statement.setString(1, currentDate)
            statement.setString(2, currentTime)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun getBarberReservationByDateTime(
        barberEmail: String,
        date: String,
        time: String
    ): Reservation? {
        var connection: Connection? = null
        var reservation: Reservation? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT * FROM reservation
                    WHERE barberEmail = ? AND date = ? AND startTime = ? AND status = 'ACCEPTED'
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            statement.setString(2, date)
            statement.setString(3, time)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                reservation = Reservation(
                    id = resultSet.getInt("id"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return reservation
    }

    fun getClientReservationByDateTime(
        clientEmail: String,
        date: String,
        time: String
    ): Reservation? {
        var connection: Connection? = null
        var reservation: Reservation? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT * FROM reservation
                    WHERE clientEmail = ? AND date = ? AND startTime = ? AND status != 'REJECTED'
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            statement.setString(2, date)
            statement.setString(3, time)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                reservation = Reservation(
                    id = resultSet.getInt("id"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return reservation
    }

    fun getRejectedReservationRequest(
        clientEmail: String,
        barberEmail: String,
        date: String,
        time: String
    ): Reservation? {
        var connection: Connection? = null
        var reservation: Reservation? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT * FROM reservation
                    WHERE clientEmail = ? AND barberEmail = ?
                    AND date = ? AND startTime = ? AND status = 'REJECTED'
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            statement.setString(2, barberEmail)
            statement.setString(3, date)
            statement.setString(4, time)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                reservation = Reservation(
                    id = resultSet.getInt("id"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return reservation
    }

    fun getClientPendingReservationRequests(
        clientEmail: String
    ): List<ExtendedReservationWithBarber> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithBarber>()
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        b.id AS barberId,
                        b.barbershopName
                    FROM reservation r
                    INNER JOIN barber b ON r.barberEmail = b.email
                    WHERE clientEmail = ? AND status = 'PENDING'
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithBarber(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    barberId = resultSet.getInt("barberId"),
                    barbershopName = resultSet.getString("barbershopName")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getClientAppointments(
        clientEmail: String
    ): List<ExtendedReservationWithBarber> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithBarber>()
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        b.id AS barberId,
                        b.barbershopName
                    FROM reservation r
                    INNER JOIN barber b ON r.barberEmail = b.email
                    WHERE clientEmail = ? AND status = 'ACCEPTED'
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithBarber(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    barberId = resultSet.getInt("barberId"),
                    barbershopName = resultSet.getString("barbershopName")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getClientRejections(
        clientEmail: String
    ): List<ExtendedReservationWithBarber> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithBarber>()
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        b.id AS barberId,
                        b.barbershopName
                    FROM reservation r
                    INNER JOIN barber b ON r.barberEmail = b.email
                    WHERE clientEmail = ? AND status = 'REJECTED'
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithBarber(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    barberId = resultSet.getInt("barberId"),
                    barbershopName = resultSet.getString("barbershopName")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getClientArchive(
        clientEmail: String
    ): List<ExtendedReservationWithBarber> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithBarber>()
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        b.id AS barberId,
                        b.barbershopName
                    FROM reservation r
                    INNER JOIN barber b ON r.barberEmail = b.email
                    WHERE clientEmail = ? AND (status = 'DONE_SUCCESS' OR status = 'DONE_FAILURE')
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithBarber(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    barberId = resultSet.getInt("barberId"),
                    barbershopName = resultSet.getString("barbershopName")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getBarberPendingReservationRequests(
        barberEmail: String
    ): List<ExtendedReservationWithClient> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithClient>()
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname
                    FROM reservation r
                    INNER JOIN client c ON r.clientEmail = c.email
                    WHERE barberEmail = ? AND status = 'PENDING'
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithClient(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    clientId = resultSet.getInt("clientId"),
                    clientName = resultSet.getString("clientName"),
                    clientSurname = resultSet.getString("clientSurname")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getBarberAppointments(
        barberEmail: String
    ): List<ExtendedReservationWithClient> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithClient>()
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname
                    FROM reservation r
                    INNER JOIN client c ON r.clientEmail = c.email
                    WHERE barberEmail = ? AND status = 'ACCEPTED'
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithClient(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    clientId = resultSet.getInt("clientId"),
                    clientName = resultSet.getString("clientName"),
                    clientSurname = resultSet.getString("clientSurname")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getBarberArchive(
        barberEmail: String
    ): List<ExtendedReservationWithClient> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithClient>()
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname
                    FROM reservation r
                    INNER JOIN client c ON r.clientEmail = c.email
                    WHERE barberEmail = ? AND (status = 'DONE_SUCCESS' OR status = 'DONE_FAILURE')
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithClient(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    clientId = resultSet.getInt("clientId"),
                    clientName = resultSet.getString("clientName"),
                    clientSurname = resultSet.getString("clientSurname")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getBarberRejections(
        barberEmail: String
    ): List<ExtendedReservationWithClient> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithClient>()
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname
                    FROM reservation r
                    INNER JOIN client c ON r.clientEmail = c.email
                    WHERE barberEmail = ? AND status = 'REJECTED'
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithClient(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    clientId = resultSet.getInt("clientId"),
                    clientName = resultSet.getString("clientName"),
                    clientSurname = resultSet.getString("clientSurname")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getBarberConfirmations(
        barberEmail: String
    ): List<ExtendedReservationWithClient> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithClient>()
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname
                    FROM reservation r
                    INNER JOIN client c ON r.clientEmail = c.email
                    WHERE barberEmail = ? AND status = 'WAITING_CONFIRMATION'
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithClient(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    clientId = resultSet.getInt("clientId"),
                    clientName = resultSet.getString("clientName"),
                    clientSurname = resultSet.getString("clientSurname")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun acceptReservationRequest(reservationId: Int) {
        var connection: Connection? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE reservation
                    SET status = 'ACCEPTED'
                    WHERE id = ?
                """.trimIndent()
            )
            statement.setInt(1, reservationId)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun rejectReservationRequest(reservationId: Int) {
        var connection: Connection? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE reservation
                    SET status = 'REJECTED'
                    WHERE id = ?
                """.trimIndent()
            )
            statement.setInt(1, reservationId)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun updateDoneReservationStatus(reservationId: Int, status: String) {
        var connection: Connection? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE reservation
                    SET status = ?
                    WHERE id = ?
                """.trimIndent()
            )
            statement.setString(1, status)
            statement.setInt(2, reservationId)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

}