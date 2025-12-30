package org.sandbox.bookshop.repository

import org.sandbox.bookshop.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, UUID>