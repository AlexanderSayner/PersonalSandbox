package com.example.graphql.service;

import com.example.graphql.model.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class BookService {
    
    private List<Book> books = new ArrayList<>();
    
    public BookService() {
        // Initialize with some sample data
        books.add(new Book("1", "The Great Gatsby", "F. Scott Fitzgerald", 1925));
        books.add(new Book("2", "To Kill a Mockingbird", "Harper Lee", 1960));
        books.add(new Book("3", "1984", "George Orwell", 1949));
        books.add(new Book("4", "Pride and Prejudice", "Jane Austen", 1813));
    }
    
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }
    
    public Optional<Book> getBookById(String id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst();
    }
    
    public List<Book> getBooksByAuthor(String author) {
        return books.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public Book addBook(Book book) {
        if (book.getId() == null) {
            // Generate a simple ID - in real applications use UUID or database-generated ID
            book.setId(String.valueOf(books.size() + 1));
        }
        books.add(book);
        return book;
    }
    
    public Book updateBook(String id, Book updatedBook) {
        Optional<Book> existingBook = getBookById(id);
        if (existingBook.isPresent()) {
            Book book = existingBook.get();
            if (updatedBook.getTitle() != null) book.setTitle(updatedBook.getTitle());
            if (updatedBook.getAuthor() != null) book.setAuthor(updatedBook.getAuthor());
            if (updatedBook.getYear() != 0) book.setYear(updatedBook.getYear());
            return book;
        }
        return null; // Book not found
    }
    
    public boolean deleteBook(String id) {
        return books.removeIf(book -> book.getId().equals(id));
    }
}