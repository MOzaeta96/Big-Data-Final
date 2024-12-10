package io.initialcapacity.collector

import org.jetbrains.exposed.sql.Table

// Table definition for Companies
object Companies : Table("companies") {
    val id = integer("id").autoIncrement()
    val stockSymbol = varchar("stock_symbol", 10)
    val companyName = varchar("company_name", 255)

    override val primaryKey = PrimaryKey(id)
}

// Table definition for StockPrices
object StockPrices : Table("stock_prices") {
    val id = integer("id").autoIncrement()
    val stockSymbol = varchar("stock_symbol", 10).references(Companies.stockSymbol)
    val date = date("date")
    val open = double("open")
    val high = double("high")
    val low = double("low")
    val close = double("close")
    val volume = long("volume")

    override val primaryKey = PrimaryKey(id)
}
