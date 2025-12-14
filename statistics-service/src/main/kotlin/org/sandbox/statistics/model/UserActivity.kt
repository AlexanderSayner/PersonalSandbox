package org.sandbox.statistics.model

import java.util.UUID
import java.time.LocalDate

data class UserActivity(
    val activityId: UUID = UUID.randomUUID(),
    val userId: UUID,
    val activityType: String,
    val activityDate: LocalDate = LocalDate.now()
)