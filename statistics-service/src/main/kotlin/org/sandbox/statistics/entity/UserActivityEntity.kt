package org.sandbox.statistics.entity

import org.springframework.data.annotation.Id
import java.time.LocalDate
import java.util.*

data class UserActivityEntity(
    @Id val activityId: UUID = UUID.randomUUID(),
    val userId: UUID,
    val activityType: String,
    val activityDate: LocalDate = LocalDate.now()
)