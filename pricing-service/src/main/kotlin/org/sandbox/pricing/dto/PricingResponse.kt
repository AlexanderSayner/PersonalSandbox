package org.sandbox.pricing.dto

import java.math.BigDecimal

data class PricingResponse(
    val productId: String,
    val basePrice: BigDecimal,
    val deliveryCost: BigDecimal,
    val totalWithDelivery: BigDecimal,
    val totalWithoutDelivery: BigDecimal
)