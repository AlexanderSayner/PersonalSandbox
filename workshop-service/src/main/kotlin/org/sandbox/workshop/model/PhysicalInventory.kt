package org.sandbox.workshop.model

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.UUID

@Table("physical_inventory")
data class PhysicalInventory(
    @PrimaryKey
    @Column("product_id")
    val productId: UUID,
    
    @Column("stock")
    var stock: Int,
    
    @Column("location")
    var location: String
)