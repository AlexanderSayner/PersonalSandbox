package com.bookshop.entity

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "product")
data class Product(
    @Id
    @UuidGenerator
    @Column(name = "product_id", updatable = false, nullable = false)
    var productId: UUID? = null,

    @Column(nullable = false)
    var title: String = "",

    @Column(length = 1000)
    var description: String? = null,

    @Column(precision = 10, scale = 2)
    var price: BigDecimal? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    var productType: ProductType = ProductType.BOOK,

    @Column(name = "library_book_id")
    var libraryBookId: UUID? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}

enum class ProductType {
    BOOK,
    DIGITAL_BOOK,
    PHYSICAL_GOOD,
    DIGITAL_GOOD
}