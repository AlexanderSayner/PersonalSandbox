package org.sandbox.pricing.dto

import java.math.BigDecimal

data class PricingRequest(
    val productId: String,
    val distance: Int,
    val weight: BigDecimal,
    val deliveryMethod: String? = null
)