package org.sandbox.statistics.consumer

import org.sandbox.statistics.entity.SalesStatisticsEntity
import org.sandbox.statistics.entity.UserActivityEntity
import org.sandbox.statistics.repository.SalesStatisticsRepository
import org.sandbox.statistics.repository.UserActivityRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.util.*

@Service
class KafkaConsumerService(
    private val salesStatisticsRepository: SalesStatisticsRepository,
    private val userActivityRepository: UserActivityRepository,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(KafkaConsumerService::class.java)

    @KafkaListener(topics = ["workshop-sales"], groupId = "statistics-group")
    suspend fun consumeSalesData(saleData: String) {
        try {
            logger.info("Received sales data: $saleData")
            val sale = objectMapper.readValue(saleData, SaleEvent::class.java)
            
            val salesEntity = SalesStatisticsEntity(
                productId = sale.productId,
                quantity = sale.quantity,
                amount = sale.amount,
                saleDate = sale.saleDate
            )
            
            salesStatisticsRepository.save(salesEntity)
            logger.info("Saved sales statistics for product ID: ${sale.productId}")
        } catch (e: Exception) {
            logger.error("Error processing sales data: ", e)
        }
    }

    @KafkaListener(topics = ["workshop-user-activity"], groupId = "statistics-group")
    suspend fun consumeUserActivityData(activityData: String) {
        try {
            logger.info("Received user activity data: $activityData")
            val activity = objectMapper.readValue(activityData, UserActivityEvent::class.java)
            
            val activityEntity = UserActivityEntity(
                userId = activity.userId,
                activityType = activity.activityType,
                activityDate = activity.activityDate
            )
            
            userActivityRepository.save(activityEntity)
            logger.info("Saved user activity for user ID: ${activity.userId}, type: ${activity.activityType}")
        } catch (e: Exception) {
            logger.error("Error processing user activity data: ", e)
        }
    }

    // Data classes for events
    data class SaleEvent(
        val saleId: UUID = UUID.randomUUID(),
        val productId: UUID,
        val quantity: Int,
        val amount: BigDecimal,
        val saleDate: java.time.LocalDate = java.time.LocalDate.now()
    )

    data class UserActivityEvent(
        val activityId: UUID = UUID.randomUUID(),
        val userId: UUID,
        val activityType: String,
        val activityDate: java.time.LocalDate = java.time.LocalDate.now()
    )
}