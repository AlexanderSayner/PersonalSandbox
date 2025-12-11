package com.example.graphql.fetcher;

import com.example.graphql.dto.BookInput;
import com.example.graphql.model.Book;
import com.example.graphql.service.BookService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AddBookDataFetcher extends BookInputHelper implements DataFetcher<Book> {

    @Inject
    private BookService bookService;

    @Override
    public Book get(DataFetchingEnvironment environment) {
        BookInput input = fetchBookInput(environment);
        Book newBook = new Book(input.getTitle(), input.getAuthor(), input.getYear()); // Create a new Book object
        return bookService.addBook(newBook); // Use BookService to add the book
    }
}
