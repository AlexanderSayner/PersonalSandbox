package org.sandbox.graphql.servlet;

import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import org.sandbox.graphql.fetcher.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@WebServlet(name = "GraphQLServlet", urlPatterns = "/graphql")
public class BookGraphQLServlet extends GraphQLHttpServlet {

    private static final Logger log = LoggerFactory.getLogger(BookGraphQLServlet.class);

    @Inject
    private AllBooksDataFetcher allBooksDataFetcher;
    @Inject
    private BookDataFetcher bookDataFetcher;
    @Inject
    private BooksByAuthorDataFetcher booksByAuthorDataFetcher;
    @Inject
    private AddBookDataFetcher addBookDataFetcher;
    @Inject
    private UpdateBookDataFetcher updateBookDataFetcher;
    @Inject
    private DeleteBookDataFetcher deleteBookDataFetcher;

    @Override
    protected GraphQLConfiguration getConfiguration() {
        try {
            return GraphQLConfiguration.with(createSchema()).build();
        } catch (IOException e) {
            log.warn(Paths.get(".").getFileName().toString());
            log.error("Could not create GraphQL schema: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private GraphQLSchema createSchema() throws IOException {
        // Load schema from the schema.graphql file
        String schema;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("graphql/schema.graphql")) {
            if (inputStream == null) {
                throw new RuntimeException("Unable to find schema.graphql");
            }
            schema = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read schema.graphql", e);
        }

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        // Define runtime wiring with data fetchers for queries and mutations
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> {
                    builder.dataFetcher("allBooks", allBooksDataFetcher);
                    builder.dataFetcher("book", bookDataFetcher); // Implement this DataFetcher
                    builder.dataFetcher("booksByAuthor", booksByAuthorDataFetcher); // Implement this DataFetcher
                    return builder;
                })
                .type("Mutation", builder -> {
                    builder.dataFetcher("addBook", addBookDataFetcher); // Implement this DataFetcher
                    builder.dataFetcher("updateBook", updateBookDataFetcher); // Implement this DataFetcher
                    builder.dataFetcher("deleteBook", deleteBookDataFetcher); // Implement this DataFetcher
                    return builder;
                })
                .build();

        // Generate the executable schema
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

}

