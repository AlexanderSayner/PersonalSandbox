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
        TODO()
    }

    suspend fun getAllUserActivities(): List<UserActivityEntity> {
        TODO()
    }

    suspend fun getSalesByProduct(productId: UUID): List<SalesStatisticsEntity> {
        // Since we don't have a direct query method, we'll get all and filter
        // In a real implementation, you would add a custom query method to the repository
        TODO()
    }

    suspend fun getUserActivitiesByUserId(userId: UUID): List<UserActivityEntity> {
        TODO()
    }

    suspend fun getSalesByDateRange(startDate: LocalDate, endDate: LocalDate): List<SalesStatisticsEntity> {
        TODO()
    }
}