package org.sandbox.statistics.service

import org.sandbox.statistics.entity.SalesStatisticsEntity
import org.sandbox.statistics.entity.UserActivityEntity
import org.sandbox.statistics.repository.SalesStatisticsRepository
import org.sandbox.statistics.repository.UserActivityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

// Use JdbcTemplate to query ClickHouse
@Service
class StatisticsService(
    val salesStatisticsRepository: SalesStatisticsRepository,
    val userActivityRepository: UserActivityRepository
) {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    fun getActivitiesByUser(userId: UUID): List<UserActivityEntity> {
        //TODO: replace the deprecated query()
        return jdbcTemplate.query(
            "SELECT activity_id, user_id, activity_type, activity_date FROM user_activity WHERE user_id = ?",
            arrayOf(userId)
        ) { rs, _ ->
            UserActivityEntity(
                activityId = UUID.fromString(rs.getString("activity_id")),
                userId = UUID.fromString(rs.getString("user_id")),
                activityType = rs.getString("activity_type"),
                activityDate = rs.getDate("activity_date").toLocalDate()
            )
        }
    }

    suspend fun getAllSalesStatistics(): List<SalesStatisticsEntity> {
        return jdbcTemplate.query(
            "SELECT sale_id, product_id, quantity, amount, sale_date FROM sales_statistics"
        ) { rs, _ ->
            SalesStatisticsEntity(
                saleId = UUID.fromString(rs.getString("sale_id")),
                productId = UUID.fromString(rs.getString("product_id")),
                quantity = rs.getInt("quantity"),
                amount = rs.getBigDecimal("amount"),
                saleDate = rs.getDate("sale_date").toLocalDate()
            )
        }
    }

    suspend fun getAllUserActivities(): List<UserActivityEntity> {
        return jdbcTemplate.query(
            "SELECT activity_id, user_id, activity_type, activity_date FROM user_activity"
        ) { rs, _ ->
            UserActivityEntity(
                activityId = UUID.fromString(rs.getString("activity_id")),
                userId = UUID.fromString(rs.getString("user_id")),
                activityType = rs.getString("activity_type"),
                activityDate = rs.getDate("activity_date").toLocalDate()
            )
        }
    }

    suspend fun getSalesByProduct(productId: UUID): List<SalesStatisticsEntity> {
        return jdbcTemplate.query(
            "SELECT sale_id, product_id, quantity, amount, sale_date FROM sales_statistics WHERE product_id = ?",
            arrayOf(productId)
        ) { rs, _ ->
            SalesStatisticsEntity(
                saleId = UUID.fromString(rs.getString("sale_id")),
                productId = UUID.fromString(rs.getString("product_id")),
                quantity = rs.getInt("quantity"),
                amount = rs.getBigDecimal("amount"),
                saleDate = rs.getDate("sale_date").toLocalDate()
            )
        }
    }

    suspend fun getUserActivitiesByUserId(userId: UUID): List<UserActivityEntity> {
        return jdbcTemplate.query(
            "SELECT activity_id, user_id, activity_type, activity_date FROM user_activity WHERE user_id = ?",
            arrayOf(userId)
        ) { rs, _ ->
            UserActivityEntity(
                activityId = UUID.fromString(rs.getString("activity_id")),
                userId = UUID.fromString(rs.getString("user_id")),
                activityType = rs.getString("activity_type"),
                activityDate = rs.getDate("activity_date").toLocalDate()
            )
        }
    }

    suspend fun getSalesByDateRange(startDate: LocalDate, endDate: LocalDate): List<SalesStatisticsEntity> {
        return jdbcTemplate.query(
            "SELECT sale_id, product_id, quantity, amount, sale_date FROM sales_statistics WHERE sale_date BETWEEN ? AND ?",
            arrayOf(startDate, endDate)
        ) { rs, _ ->
            SalesStatisticsEntity(
                saleId = UUID.fromString(rs.getString("sale_id")),
                productId = UUID.fromString(rs.getString("product_id")),
                quantity = rs.getInt("quantity"),
                amount = rs.getBigDecimal("amount"),
                saleDate = rs.getDate("sale_date").toLocalDate()
            )
        }
    }
}