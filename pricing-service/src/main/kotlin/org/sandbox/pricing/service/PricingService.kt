package org.sandbox.pricing.service

import org.sandbox.pricing.client.BookshopGrpcClient
import org.sandbox.pricing.dto.PricingRequest
import org.sandbox.pricing.dto.PricingResponse
import org.sandbox.pricing.repository.DeliveryMethodRepository
import org.sandbox.pricing.repository.PricingRuleRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PricingService(
    val bookshopGrpcClient: BookshopGrpcClient,
    val pricingRuleRepository: PricingRuleRepository,
    val deliveryMethodRepository: DeliveryMethodRepository
) {
    // TODO: support coroutines
    suspend fun calculatePricing(pricingRequest: PricingRequest): PricingResponse/* = coroutineScope*/ {
        // Get the base product price from the bookshop service
        val basePrice = bookshopGrpcClient.getProductPrice(pricingRequest.productId).toBigDecimal()

        // Calculate delivery cost based on distance, weight and delivery method
        val deliveryCost = calculateDeliveryCost(pricingRequest)

        // Calculate totals
        val totalWithDelivery = basePrice.add(deliveryCost)
        val totalWithoutDelivery = basePrice

        return PricingResponse(
            productId = pricingRequest.productId,
            basePrice = basePrice,
            deliveryCost = deliveryCost,
            totalWithDelivery = totalWithDelivery,
            totalWithoutDelivery = totalWithoutDelivery
        )
    }

    private fun calculateDeliveryCost(pricingRequest: PricingRequest): BigDecimal {
        // Find the appropriate delivery method
        val deliveryMethod = if (pricingRequest.deliveryMethod != null) {
            deliveryMethodRepository.findByName(pricingRequest.deliveryMethod)
        } else {
            // Default to Standard if no specific method is requested
            deliveryMethodRepository.findByName("Standard")
        }

        if (deliveryMethod == null) {
            throw IllegalArgumentException("Delivery method ${pricingRequest.deliveryMethod} not found")
        }

        // Find the pricing rule that applies based on distance and weight
        val applicableRules =
            pricingRuleRepository.findByMethodIdAndMinDistanceLessThanEqualAndMaxDistanceGreaterThanEqual(
                deliveryMethod.methodId,
                pricingRequest.distance,
                pricingRequest.distance
            ).filter { rule ->
                if (rule.minWeight != null && rule.maxWeight != null) {
                    pricingRequest.weight >= rule.minWeight && pricingRequest.weight <= rule.maxWeight
                } else true
            }

        return if (applicableRules.isNotEmpty()) {
            // Return the cost of the first matching rule (you might want more sophisticated logic here)
            applicableRules.first().cost ?: BigDecimal.ZERO
        } else {
            // If no specific rule matches, return a default cost or throw an exception
            BigDecimal.ZERO
        }
    }
}