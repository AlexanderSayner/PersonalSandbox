package org.sandbox.statistics.model

import java.util.UUID
import java.math.BigDecimal
import java.time.LocalDate

data class SalesStatistics(
    val saleId: UUID = UUID.randomUUID(),
    val productId: UUID,
    val quantity: Int,
    val amount: BigDecimal,
    val saleDate: LocalDate = LocalDate.now()
)