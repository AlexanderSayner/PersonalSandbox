package org.sandbox.statistics.service

import org.sandbox.statistics.entity.SalesStatisticsEntity
import org.sandbox.statistics.entity.UserActivityEntity
import org.sandbox.statistics.repository.SalesStatisticsRepository
import org.sandbox.statistics.repository.UserActivityRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class StatisticsService(
    private val salesStatisticsRepository: SalesStatisticsRepository,
    private val userActivityRepository: UserActivityRepository
) {

    suspend fun getAllSalesStatistics(): List<SalesStatisticsEntity> {
        return salesStatisticsRepository.findAll().toList()
    }

    suspend fun getAllUserActivities(): List<UserActivityEntity> {
        return userActivityRepository.findAll().toList()
    }

    suspend fun getSalesByProduct(productId: UUID): List<SalesStatisticsEntity> {
        // Since we don't have a direct query method, we'll get all and filter
        // In a real implementation, you would add a custom query method to the repository
        return salesStatisticsRepository.findAll().filter { it.productId == productId }.toList()
    }

    suspend fun getUserActivitiesByUserId(userId: UUID): List<UserActivityEntity> {
        return userActivityRepository.findAll().filter { it.userId == userId }.toList()
    }

    suspend fun getSalesByDateRange(startDate: LocalDate, endDate: LocalDate): List<SalesStatisticsEntity> {
        return salesStatisticsRepository.findAll()
            .filter { it.saleDate >= startDate && it.saleDate <= endDate }
            .toList()
    }
}