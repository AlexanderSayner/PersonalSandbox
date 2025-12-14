package org.sandbox.statistics.entity

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class SalesStatisticsEntity(
    val saleId: UUID = UUID.randomUUID(),
    val productId: UUID,
    val quantity: Int,
    val amount: BigDecimal,
    val saleDate: LocalDate = LocalDate.now()
)