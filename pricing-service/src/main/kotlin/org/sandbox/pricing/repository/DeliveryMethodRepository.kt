package org.sandbox.pricing.repository

import org.sandbox.pricing.entity.DeliveryMethod
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DeliveryMethodRepository : JpaRepository<DeliveryMethod, UUID> {
    fun findByName(name: String): DeliveryMethod?
}