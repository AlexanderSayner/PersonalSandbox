package com.example.graphql;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// Standard JAX-RS Application class to enable JAX-RS endpoints
@ApplicationPath("/graphql")
public class GraphQLApplication extends Application {
}
