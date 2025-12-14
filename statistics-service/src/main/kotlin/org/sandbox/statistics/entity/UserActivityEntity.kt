package org.sandbox.statistics.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
import java.time.LocalDate

@Table("user_activity")
data class UserActivityEntity(
    @Id
    @Column("activity_id")
    val activityId: UUID = UUID.randomUUID(),
    
    @Column("user_id")
    val userId: UUID,
    
    @Column("activity_type")
    val activityType: String,
    
    @Column("activity_date")
    val activityDate: LocalDate = LocalDate.now()
)