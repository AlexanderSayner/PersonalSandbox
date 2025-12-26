package org.sandbox.statistics.graphql

import org.sandbox.statistics.entity.SalesStatisticsEntity
import org.sandbox.statistics.entity.UserActivityEntity
import org.sandbox.statistics.service.StatisticsService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.time.LocalDate
import java.util.*

@Controller
class StatisticsGraphQLResolver(
    private val statisticsService: StatisticsService
) {

    @QueryMapping
    suspend fun salesStatistics(): List<SalesStatisticsEntity> {
        return statisticsService.getAllSalesStatistics()
    }

    @QueryMapping
    suspend fun userActivities(): List<UserActivityEntity> {
        return statisticsService.getAllUserActivities()
    }

    @QueryMapping
    suspend fun salesByProduct(@Argument productId: String): List<SalesStatisticsEntity> {
        return statisticsService.getSalesByProduct(UUID.fromString(productId))
    }

    @QueryMapping
    suspend fun activitiesByUser(@Argument userId: String): List<UserActivityEntity> {
        return statisticsService.getUserActivitiesByUserId(UUID.fromString(userId))
    }

    @QueryMapping
    suspend fun salesByDateRange(
        @Argument startDate: String,
        @Argument endDate: String
    ): List<SalesStatisticsEntity> {
        return statisticsService.getSalesByDateRange(
            LocalDate.parse(startDate),
            LocalDate.parse(endDate)
        )
    }
}