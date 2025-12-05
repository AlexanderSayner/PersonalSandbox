package com.example.graphql;

import com.example.graphql.model.Book;
import com.example.graphql.service.BookService;
import org.eclipse.microprofile.graphql.*;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@GraphQLApi
public class BookGraphQLApi {
    
    @Inject
    private BookService bookService;
    
    @Query("allBooks")
    @Description("Get all books in the library")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }
    
    @Query("book")
    @Description("Get a book by its ID")
    public Book getBook(@Name("id") Long id) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            return book.get();
        } else {
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
    }
    
    @Query("booksByAuthor")
    @Description("Find books by author name")
    public List<Book> getBooksByAuthor(@Name("author") String author) {
        return bookService.getBooksByAuthor(author);
    }
    
    @Mutation
    @Description("Add a new book to the library")
    public Book addBook(@Name("input") BookInput input) {
        Book book = new Book(input.getTitle(), input.getAuthor(), input.getYear());
        return bookService.addBook(book);
    }
    
    @Mutation
    @Description("Update an existing book")
    public Book updateBook(@Name("id") Long id, @Name("input") BookInput input) {
        Book updatedBook = new Book();
        updatedBook.setTitle(input.getTitle());
        updatedBook.setAuthor(input.getAuthor());
        updatedBook.setYear(input.getYear());
        Book result = bookService.updateBook(id, updatedBook);
        if (result == null) {
            throw new BookNotFoundException("Book with ID " + id + " not found for update");
        }
        return result;
    }
    
    @Mutation
    @Description("Delete a book by ID")
    public boolean deleteBook(@Name("id") Long id) {
        boolean deleted = bookService.deleteBook(id);
        if (!deleted) {
            throw new BookNotFoundException("Book with ID " + id + " not found for deletion");
        }
        return true;
    }
    
    // Input class for mutations
    public static class BookInput {
        private String title;
        private String author;
        private int year;
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getAuthor() {
            return author;
        }
        
        public void setAuthor(String author) {
            this.author = author;
        }
        
        public int getYear() {
            return year;
        }
        
        public void setYear(int year) {
            this.year = year;
        }
    }
    
    // Custom exception for not found errors
    public static class BookNotFoundException extends RuntimeException {
        public BookNotFoundException(String message) {
            super(message);
        }
    }
}