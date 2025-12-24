package org.sandbox.statistics.entity

import org.springframework.data.annotation.Id
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class SalesStatisticsEntity(
    @Id val saleId: UUID = UUID.randomUUID(),
    val productId: UUID,
    val quantity: Int,
    val amount: BigDecimal,
    val saleDate: LocalDate = LocalDate.now()
)