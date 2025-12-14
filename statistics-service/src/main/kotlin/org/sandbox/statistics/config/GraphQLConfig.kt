package org.sandbox.statistics.config

import graphql.GraphQL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphQLConfig {

    @Bean
    fun graphQL(graphQLSchema: graphql.schema.GraphQLSchema): GraphQL {
        return GraphQL.newGraphQL(graphQLSchema).build()
    }
}