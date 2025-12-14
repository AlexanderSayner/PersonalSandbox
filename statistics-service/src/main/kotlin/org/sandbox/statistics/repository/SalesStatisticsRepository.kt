package org.sandbox.statistics.repository

import org.sandbox.statistics.entity.SalesStatisticsEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface SalesStatisticsRepository : CoroutineCrudRepository<SalesStatisticsEntity, UUID>