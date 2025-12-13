package com.bookshop.dto

import java.math.BigDecimal
import java.util.*

data class OrderItemDto(
    val orderItemId: UUID? = null,
    val orderId: UUID? = null,
    val productId: UUID? = null,
    val quantity: Int = 0,
    val price: BigDecimal? = null
)