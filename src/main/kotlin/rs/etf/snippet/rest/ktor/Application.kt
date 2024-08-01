package rs.etf.snippet.rest.ktor

import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import rs.etf.snippet.rest.ktor.plugins.*

fun main() {
    embeddedServer(
        Netty,
        host = "0.0.0.0",
        port = 8080,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    DatabaseFactory.dataSource
    configureSerialization()
    configureRouting()
    environment.monitor.subscribe(ApplicationStopping) {
        (DatabaseFactory.dataSource as HikariDataSource).close()
    }
}
