package com.example.graphql.service;

import com.example.graphql.model.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookService {

    @PersistenceContext
    private EntityManager em;

    public List<Book> getAllBooks() {
        try {
            return em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all books", e);
        }
    }

    public Optional<Book> getBookById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Book ID must be a positive number");
        }
        try {
            Book book = em.find(Book.class, id);
            return Optional.ofNullable(book);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving book with ID: " + id, e);
        }
    }

    public List<Book> getBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be null or empty");
        }
        try {
            return em.createQuery("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(:author)", Book.class)
                    .setParameter("author", "%" + author + "%")
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving books by author: " + author, e);
        }
    }

    @Transactional
    public Book addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be null or empty");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author cannot be null or empty");
        }
        try {
            em.persist(book);
            em.flush(); // Ensure the ID is generated
            return book;
        } catch (Exception e) {
            throw new RuntimeException("Error adding book: " + book.getTitle(), e);
        }
    }

    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Book ID must be a positive number");
        }
        if (updatedBook == null) {
            throw new IllegalArgumentException("Updated book cannot be null");
        }
        try {
            Book book = em.find(Book.class, id);
            if (book != null) {
                book.setTitle(updatedBook.getTitle());
                book.setAuthor(updatedBook.getAuthor());
                book.setYear(updatedBook.getYear());
                em.merge(book);
                return book;
            }
            return null; // Book not found
        } catch (Exception e) {
            throw new RuntimeException("Error updating book with ID: " + id, e);
        }
    }

    @Transactional
    public boolean deleteBook(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Book ID must be a positive number");
        }
        try {
            Book book = em.find(Book.class, id);
            if (book != null) {
                em.remove(book);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting book with ID: " + id, e);
        }
    }
}