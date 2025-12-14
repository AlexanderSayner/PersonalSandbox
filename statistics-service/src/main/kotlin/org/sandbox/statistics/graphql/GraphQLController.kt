package org.sandbox.statistics.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionInput
import graphql.GraphQL
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/graphql")
class GraphQLController(
    private val graphQL: GraphQL,
    private val statisticsService: org.sandbox.statistics.service.StatisticsService,
    private val objectMapper: ObjectMapper
) {

    @PostMapping
    suspend fun executeQuery(@RequestBody request: Map<String, Any>, httpRequest: HttpServletRequest): Map<String, Any> {
        val query = request["query"] as String
        val variables = request["variables"] as? Map<String, Any> ?: emptyMap()

        val executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .variables(variables)
            .context(GraphQLContext(statisticsService))
            .build()

        val executionResult = graphQL.execute(executionInput)
        return executionResult.toSpecification()
    }
}