package org.sandbox.graphql.fetcher;

import org.sandbox.graphql.dto.BookInput;
import org.sandbox.graphql.model.Book;
import org.sandbox.graphql.service.BookService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UpdateBookDataFetcher extends BookInputHelper implements DataFetcher<Book> {

    @Inject
    private BookService bookService;

    @Override
    public Book get(DataFetchingEnvironment environment) {
        String id = environment.getArgument("id"); // Get the ID of the book to be updated
        BookInput input = fetchBookInput(environment);

        // Create a new Book object from the input
        Book updatedBook = new Book();
        assert input != null;
        updatedBook.setTitle(input.getTitle());
        updatedBook.setAuthor(input.getAuthor());
        updatedBook.setYear(input.getYear());

        assert id != null;
        return bookService.updateBook(Long.parseLong(id), updatedBook);
    }
}
