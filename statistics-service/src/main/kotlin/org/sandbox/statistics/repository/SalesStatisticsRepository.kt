package org.sandbox.statistics.repository

import org.sandbox.statistics.entity.SalesStatisticsEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class SalesStatisticsRepository(val jdbcTemplate: JdbcTemplate) {
    fun save(salesEntity: SalesStatisticsEntity) {
        jdbcTemplate.update(
            """
        INSERT INTO SalesStatisticsEntity (saleId, productId, quantity, amount, saleDate)
        VALUES (?, ?, ?, ?, ?);
    """,
            salesEntity.saleId,
            salesEntity.productId,
            salesEntity.quantity,
            salesEntity.amount,
            salesEntity.saleDate
        )
    }

}