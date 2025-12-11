package com.example.graphql.fetcher;

import com.example.graphql.model.Book;
import com.example.graphql.service.BookService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class AllBooksDataFetcher implements DataFetcher<List<Book>> {
    @Inject
    private BookService bookService;

    @Override
    public List<Book> get(DataFetchingEnvironment environment) {
        return bookService.getAllBooks();
    }
}

