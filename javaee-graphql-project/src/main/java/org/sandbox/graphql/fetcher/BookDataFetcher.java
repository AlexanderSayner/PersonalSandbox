package org.sandbox.graphql.fetcher;

import org.sandbox.graphql.model.Book;
import org.sandbox.graphql.service.BookService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BookDataFetcher implements DataFetcher<Book> {

    @Inject
    private BookService bookService;

    @Override
    public Book get(DataFetchingEnvironment environment) {
        String id = environment.getArgument("id"); // Get the argument from the query
        assert id != null;
        return bookService.getBookById(Long.parseLong(id)).orElse(null); // Return the book or null if not found
    }
}
