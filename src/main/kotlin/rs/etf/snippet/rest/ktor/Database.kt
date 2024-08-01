package rs.etf.snippet.rest.ktor

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object DatabaseFactory {
    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:mysql://localhost:3306/barberbooker"
        username = "root"
        password = "root"
        driverClassName = "com.mysql.cj.jdbc.Driver"
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }

    val dataSource: DataSource = HikariDataSource(hikariConfig)
}