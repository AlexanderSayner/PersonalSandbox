package org.sandbox.statistics.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
@EnableJdbcRepositories(basePackages = ["org.sandbox.statistics.repository"])
class ClickHouseConfig : AbstractJdbcConfiguration() {

    override fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver")
        dataSource.url = System.getenv("CLICKHOUSE_URL") ?: "jdbc:clickhouse://localhost:8123/default"
        dataSource.username = System.getenv("CLICKHOUSE_USER") ?: "default"
        dataSource.password = System.getenv("CLICKHOUSE_PASSWORD") ?: ""
        return dataSource
    }
}