package com.example.graphql;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

// Standard JAX-RS Application class to enable JAX-RS endpoints
@ApplicationPath("/")
public class GraphQLApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // The @GraphQLApi annotated classes will be automatically picked up
        // by the MicroProfile GraphQL implementation in WildFly
        return classes;
    }
}