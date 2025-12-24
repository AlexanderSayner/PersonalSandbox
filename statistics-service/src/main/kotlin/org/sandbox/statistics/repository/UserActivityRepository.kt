package org.sandbox.statistics.repository

import org.sandbox.statistics.entity.UserActivityEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class UserActivityRepository(val jdbcTemplate: JdbcTemplate) {
    fun save(activityEntity: UserActivityEntity) {
        jdbcTemplate.update(
            """
        INSERT INTO UserActivityEntity (activityId, userId, activityType, activityDate)
        VALUES (?, ?, ?, ?);
    """,
            activityEntity.activityId,
            activityEntity.userId,
            activityEntity.activityType,
            activityEntity.activityDate
        )
    }

}