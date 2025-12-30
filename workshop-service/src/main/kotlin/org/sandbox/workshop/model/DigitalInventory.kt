package org.sandbox.workshop.model

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.UUID

@Table("digital_inventory")
data class DigitalInventory(
    @PrimaryKey
    @Column("product_id")
    val productId: UUID,
    
    @Column("digital_file")
    var digitalFile: String,
    
    @Column("licenses_total")
    var licensesTotal: Int,
    
    @Column("licenses_sold")
    var licensesSold: Int
)