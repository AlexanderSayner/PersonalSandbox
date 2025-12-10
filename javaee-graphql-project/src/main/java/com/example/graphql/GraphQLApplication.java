package com.example.graphql;

import io.smallrye.graphql.cdi.producer.GraphQLProducer;
import io.smallrye.graphql.jaxrs.SmallRyeGraphQLApi;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/graphql")
public class GraphQLApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Register the SmallRye GraphQL API endpoint
        classes.add(SmallRyeGraphQLApi.class);
        // Add other JAX-RS resources if needed
        return classes;
    }
}