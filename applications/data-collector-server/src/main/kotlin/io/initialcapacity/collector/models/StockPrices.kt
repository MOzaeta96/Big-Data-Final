package io.initialcapacity.collector.models

import java.time.LocalDate

data class StockPrices(
    val stockSymbol: String,
    val date: LocalDate,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)
