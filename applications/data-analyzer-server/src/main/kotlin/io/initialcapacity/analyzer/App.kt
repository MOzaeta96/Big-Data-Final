package io.initialcapacity.analyzer

import io.initialcapacity.workflow.WorkScheduler
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import java.util.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import io.initialcapacity.collector.DatabaseModels


fun Application.module() {
    install(Routing) {
        get("/") {
            call.respondText("hi!", ContentType.Text.Html)
        }
    }
    val scheduler = WorkScheduler<ExampleTask>(ExampleWorkFinder(), mutableListOf(ExampleWorker()), 30)
    scheduler.start()
}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8887
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = { module() }).start(wait = true)
    MessageConsumer.consume()
}

fun initDatabase() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/stock_data", // Your database URL here
        driver = "org.postgresql.Driver",
        user = "your_username", // Replace with your username
        password = "your_password" // Replace with your password
    )
    transaction {
        // Initialize the database schema (if not already created)
        SchemaUtils.create(Companies, StockPrices)
    }
}