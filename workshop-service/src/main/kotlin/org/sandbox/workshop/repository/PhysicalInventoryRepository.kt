package org.sandbox.workshop.repository

import org.sandbox.workshop.model.PhysicalInventory
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PhysicalInventoryRepository : CassandraRepository<PhysicalInventory, UUID> {
    fun findByProductId(productId: UUID): PhysicalInventory?
}