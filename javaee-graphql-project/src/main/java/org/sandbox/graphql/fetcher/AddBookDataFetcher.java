package org.sandbox.graphql.fetcher;

import org.sandbox.graphql.dto.BookInput;
import org.sandbox.graphql.model.Book;
import org.sandbox.graphql.service.BookService;
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
