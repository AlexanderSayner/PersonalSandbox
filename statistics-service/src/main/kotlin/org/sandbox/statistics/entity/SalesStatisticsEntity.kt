package org.sandbox.statistics.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
import java.math.BigDecimal
import java.time.LocalDate

@Table("sales_statistics")
data class SalesStatisticsEntity(
    @Id
    @Column("sale_id")
    val saleId: UUID = UUID.randomUUID(),
    
    @Column("product_id")
    val productId: UUID,
    
    @Column("quantity")
    val quantity: Int,
    
    @Column("amount")
    val amount: BigDecimal,
    
    @Column("sale_date")
    val saleDate: LocalDate = LocalDate.now()
)