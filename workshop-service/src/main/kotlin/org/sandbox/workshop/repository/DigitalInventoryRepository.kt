package org.sandbox.workshop.repository

import org.sandbox.workshop.model.DigitalInventory
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DigitalInventoryRepository : CassandraRepository<DigitalInventory, UUID> {
    fun findByProductId(productId: UUID): DigitalInventory?
}