import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.LocalDate

fun ingestCompanies(filePath: String) {
    val companies = File(filePath).readLines().drop(1) // Skip header row
    val message = "New company data ingested at ${System.currentTimeMillis()}"
    MessagePublisher.publish(message)
    transaction {
        companies.forEach { line ->
            val (symbol, name) = line.split(',')
            Companies.insert {
                it[stockSymbol] = symbol
                it[companyName] = name
            }
        }
    }
}

fun ingestStockPrices(filePath: String) {
    val stockPrices = File(filePath).readLines().drop(1) // Skip header row
    transaction {
        stockPrices.forEach { line ->
            val (symbol, date, open, high, low, close, adjClose, volume) = line.split(',')
            val message = "New stock data ingested at ${System.currentTimeMillis()}"
            MessagePublisher.publish(message)
            StockPrices.insert {
                it[stockSymbol] = symbol
                it[StockPrices.date] = LocalDate.parse(date)
                it[StockPrices.open] = open.toBigDecimal()
                it[StockPrices.high] = high.toBigDecimal()
                it[StockPrices.low] = low.toBigDecimal()
                it[StockPrices.close] = close.toBigDecimal()
                it[StockPrices.adjClose] = adjClose.toBigDecimal()
                it[StockPrices.volume] = volume.toLong()
            }
        }
    }
}
