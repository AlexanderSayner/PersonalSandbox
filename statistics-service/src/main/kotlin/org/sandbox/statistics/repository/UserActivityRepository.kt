package org.sandbox.statistics.repository

import org.sandbox.statistics.entity.UserActivityEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface UserActivityRepository : CoroutineCrudRepository<UserActivityEntity, UUID>