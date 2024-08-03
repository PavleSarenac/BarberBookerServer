package rs.etf.snippet.rest.ktor.daos

import rs.etf.snippet.rest.ktor.DatabaseFactory
import rs.etf.snippet.rest.ktor.entities.tables.Client
import java.sql.Connection


object ClientDao {

    fun addNewClient(client: Client) {
        var connection: Connection? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    INSERT INTO client (
                        email, 
                        password,
                        name,
                        surname,
                        fcmToken
                    )
                    VALUES (?, ?, ?, ?)
                """.trimIndent()
            )
            statement.setString(1, client.email)
            statement.setString(2, client.password)
            statement.setString(3, client.name)
            statement.setString(4, client.surname)
            statement.setString(5, client.fcmToken)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun getClientByEmail(email: String): Client? {
        var connection: Connection? = null
        var client: Client? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT *
                    FROM client
                    WHERE email = ?
                """.trimIndent()
            )
            statement.setString(1, email)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                client = Client(
                    id = resultSet.getInt("id"),
                    email = resultSet.getString("email"),
                    password = resultSet.getString("password"),
                    name = resultSet.getString("name"),
                    surname = resultSet.getString("surname"),
                    fcmToken = resultSet.getString("fcmToken")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return client
    }

    fun getClientByEmailAndPassword(email: String, hashedPassword: String): Client? {
        var connection: Connection? = null
        var client: Client? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT *
                    FROM client
                    WHERE email = ? AND password = ?
                """.trimIndent()
            )
            statement.setString(1, email)
            statement.setString(2, hashedPassword)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                client = Client(
                    id = resultSet.getInt("id"),
                    email = resultSet.getString("email"),
                    password = resultSet.getString("password"),
                    name = resultSet.getString("name"),
                    surname = resultSet.getString("surname"),
                    fcmToken = resultSet.getString("fcmToken")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return client
    }

    fun updateClientProfile(email: String, name: String, surname: String) {
        var connection: Connection? = null
        try {
            connection = DatabaseFactory.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE client
                    SET name = ?, surname = ?
                    WHERE email = ?
                """.trimIndent()
            )
            statement.setString(1, name)
            statement.setString(2, surname)
            statement.setString(3, email)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

}