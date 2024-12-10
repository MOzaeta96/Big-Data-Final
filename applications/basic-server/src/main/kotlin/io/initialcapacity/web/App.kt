package io.initialcapacity.web

import freemarker.cache.ClassTemplateLoader
import io.initialcapacity.collector.DatabaseModels.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.ktor.server.plugins.metrics.micrometer.*
import java.time.LocalDate
import java.util.*
import kotlin.collections.set

private val logger = LoggerFactory.getLogger(object {}.javaClass.enclosingClass)

// Initialize Prometheus Registry
val prometheusRegistry = PrometheusMeterRegistry(io.micrometer.prometheus.PrometheusConfig.DEFAULT)

fun Application.module() {
    logger.info("starting the app")

    // Initialize the database
    initDatabase() // Database initialization function

    // Install Prometheus Metrics
    install(MicrometerMetrics) {
        registry = prometheusRegistry
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Routing) {
        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("headers" to headers())))
        }

        // Serve static resources
        staticResources("/static/styles", "static/styles")
        staticResources("/static/images", "static/images")

        // API Routes
        get("/api/companies") {
            val companies = transaction {
                Companies.selectAll().map { it[Companies.companyName] }
            }
            call.respond(companies)
        }

        get("/api/stocks") {
            val symbol = call.request.queryParameters["symbol"]
            val date = call.request.queryParameters["date"]
            val stocks = transaction {
                StockPrices.select {
                    (StockPrices.stockSymbol eq symbol) and (StockPrices.date eq LocalDate.parse(date))
                }.map { it[StockPrices.close] }
            }
            call.respond(stocks)
        }

        // Metrics endpoint
        get("/metrics") {
            call.respond(prometheusRegistry.scrape())
        }
    }
}

// Database initialization function
fun initDatabase() {
    org.jetbrains.exposed.sql.Database.connect(
        "jdbc:postgresql://localhost:5432/stock_data", // Database URL
        driver = "org.postgresql.Driver",
        user = "your_username", // Replace with your PostgreSQL username
        password = "your_password" // Replace with your PostgreSQL password
    )
}

private fun PipelineContext<Unit, ApplicationCall>.headers(): MutableMap<String, String> {
    val headers = mutableMapOf<String, String>()
    call.request.headers.entries().forEach { entry ->
        headers[entry.key] = entry.value.joinToString()
    }
    return headers
}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8888
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = { module() }).start(wait = true)
}
