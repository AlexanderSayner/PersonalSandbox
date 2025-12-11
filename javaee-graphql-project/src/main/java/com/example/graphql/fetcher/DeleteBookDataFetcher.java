package com.example.graphql.fetcher;

import com.example.graphql.service.BookService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

@ApplicationScoped
public class DeleteBookDataFetcher implements DataFetcher<Boolean> {

    @Inject
    private BookService bookService;

    @Override
    public Boolean get(DataFetchingEnvironment environment) {
        String id = environment.getArgument("id"); // Get the ID of the book to delete
        assert id != null;
        return bookService.deleteBook(Long.parseLong(id));
    }
}
