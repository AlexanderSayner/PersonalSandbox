package org.sandbox.statistics

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [JdbcRepositoriesAutoConfiguration::class])
class StatisticsApplication

fun main(args: Array<String>) {
    runApplication<StatisticsApplication>(*args)
}