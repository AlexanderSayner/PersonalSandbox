package org.sandbox.pricing.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "delivery_methods")
data class DeliveryMethod(
    @Id
    @Column(name = "method_id")
    val methodId: UUID? = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "description")
    var description: String? = null
)