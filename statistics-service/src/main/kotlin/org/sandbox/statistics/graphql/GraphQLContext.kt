package org.sandbox.statistics.graphql

import org.sandbox.statistics.entity.SalesStatisticsEntity
import org.sandbox.statistics.entity.UserActivityEntity
import org.sandbox.statistics.service.StatisticsService
import java.time.LocalDate
import java.util.*

class GraphQLContext(private val statisticsService: StatisticsService) {

    suspend fun getSalesStatistics(): List<SalesStatisticsEntity> {
        return statisticsService.getAllSalesStatistics()
    }

    suspend fun getUserActivities(): List<UserActivityEntity> {
        return statisticsService.getAllUserActivities()
    }

    suspend fun getSalesByProduct(productId: String): List<SalesStatisticsEntity> {
        return statisticsService.getSalesByProduct(UUID.fromString(productId))
    }

    suspend fun getActivitiesByUser(userId: String): List<UserActivityEntity> {
        return statisticsService.getUserActivitiesByUserId(UUID.fromString(userId))
    }

    suspend fun getSalesByDateRange(startDate: String, endDate: String): List<SalesStatisticsEntity> {
        return statisticsService.getSalesByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate))
    }
}