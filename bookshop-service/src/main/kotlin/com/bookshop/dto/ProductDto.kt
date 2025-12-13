package com.bookshop.dto

import java.math.BigDecimal
import java.util.*

data class ProductDto(
    val productId: UUID? = null,
    val title: String = "",
    val description: String? = null,
    val price: BigDecimal? = null,
    val productType: String = "",
    val libraryBookId: UUID? = null
)