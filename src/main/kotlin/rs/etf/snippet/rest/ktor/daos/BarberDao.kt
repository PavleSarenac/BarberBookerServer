package rs.etf.snippet.rest.ktor.daos

import rs.etf.snippet.rest.ktor.DatabaseFactory
import rs.etf.snippet.rest.ktor.entities.tables.Barber
import java.sql.Connection

object BarberDao {

    fun addNewBarber(barber: Barber) {
        var connection: Connection? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    INSERT INTO barber (
                        email, 
                        password,
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
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
            )
            statement.setString(1, barber.email)
            statement.setString(2, barber.password)
            statement.setString(3, barber.barbershopName)
            statement.setDouble(4, barber.price)
            statement.setString(5, barber.phone)
            statement.setString(6, barber.country)
            statement.setString(7, barber.city)
            statement.setString(8, barber.municipality)
            statement.setString(9, barber.address)
            statement.setString(10, barber.workingDays)
            statement.setString(11, barber.workingHours)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

}