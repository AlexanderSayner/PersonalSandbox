package org.sandbox.statistics.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class ClickHouseConfig {

    @Value($$"${spring.datasource.url}")
    private lateinit var clickhouseUrl: String

    @Value($$"${spring.datasource.username}")
    private lateinit var clickhouseUser: String

    @Value($$"${spring.datasource.password}")
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
