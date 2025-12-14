package org.sandbox.statistics.entity

import java.time.LocalDate
import java.util.*

data class UserActivityEntity(
    val activityId: UUID = UUID.randomUUID(),
    val userId: UUID,
    val activityType: String,
    val activityDate: LocalDate = LocalDate.now()
)