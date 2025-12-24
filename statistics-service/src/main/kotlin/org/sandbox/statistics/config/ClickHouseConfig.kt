package org.sandbox.statistics.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class ClickHouseConfig {

    @Value("${clickhouse.url:jdbc:clickhouse://localhost:8123/default}")
    private lateinit var clickhouseUrl: String

    @Value("${clickhouse.username:default}")
    private lateinit var clickhouseUser: String

    @Value("${clickhouse.password:}")
    private lateinit var clickhousePassword: String

    @Bean
    fun clickHouseDataSource(): DataSource {
        return DriverManagerDataSource(clickhouseUrl, clickhouseUser, clickhousePassword)
    }

    @Bean
    fun clickHouseJdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }
}
