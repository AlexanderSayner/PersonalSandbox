package org.sandbox.statistics.graphql

import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.TypeDefinitionRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class GraphQLSchemaConfig {

    @Bean
    fun graphQLSchema(): graphql.schema.GraphQLSchema {
        val schemaString = """
            type Query {
                salesStatistics: [SalesStatistics!]!
                userActivities: [UserActivity!]!
                salesByProduct(productId: ID!): [SalesStatistics!]!
                activitiesByUser(userId: ID!): [UserActivity!]!
                salesByDateRange(startDate: String!, endDate: String!): [SalesStatistics!]!
            }

            type SalesStatistics {
                saleId: ID!
                productId: ID!
                quantity: Int!
                amount: Float!
                saleDate: String!
            }

            type UserActivity {
                activityId: ID!
                userId: ID!
                activityType: String!
                activityDate: String!
            }
        """.trimIndent()

        val typeRegistry = graphql.schema.idl.SchemaParser().parse(schemaString)
//        val runtimeWiring = RuntimeWiring.newRuntimeWiring()
//            .type("Query") { builder ->
//                builder
//                    .dataFetcher("salesStatistics") { environment ->
//                        environment.getContext<GraphQLContext>().getSalesStatistics()
//                    }
//                    .dataFetcher("userActivities") { environment ->
//                        environment.getContext<GraphQLContext>().getUserActivities()
//                    }
//                    .dataFetcher("salesByProduct") { environment ->
//                        val productId = environment.getArgument<String>("productId")
//                        environment.getContext<GraphQLContext>().getSalesByProduct(productId)
//                    }
//                    .dataFetcher("activitiesByUser") { environment ->
//                        val userId = environment.getArgument<String>("userId")
//                        environment.getContext<GraphQLContext>().getActivitiesByUser(userId)
//                    }
//                    .dataFetcher("salesByDateRange") { environment ->
//                        val startDate = environment.getArgument<String>("startDate")
//                        val endDate = environment.getArgument<String>("endDate")
//                        environment.getContext<GraphQLContext>().getSalesByDateRange(startDate, endDate)
//                    }
//            }
//            .build()

        val schemaGenerator = SchemaGenerator()
//        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)
        TODO()
    }
}