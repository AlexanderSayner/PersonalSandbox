package com.bookshop.dto

import java.math.BigDecimal
import java.util.*

data class OrderDto(
    val orderId: UUID? = null,
    val userId: UUID? = null,
    val status: String = "",
    val totalAmount: BigDecimal? = null
)