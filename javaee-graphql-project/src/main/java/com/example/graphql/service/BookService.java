package com.example.graphql.service;

import com.example.graphql.model.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);
    @PersistenceContext
    private EntityManager em;

    public List<Book> getAllBooks() {
        return em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
    }

    public Optional<Book> getBookById(Long id) {
        Book book = em.find(Book.class, id);
        return Optional.ofNullable(book);
    }

    public List<Book> getBooksByAuthor(String author) {
        return em.createQuery("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(:author)", Book.class)
                .setParameter("author", "%" + author + "%")
                .getResultList();
    }

    @Transactional
    public Book addBook(Book book) {
        em.persist(book);
        return book;
    }

    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setYear(updatedBook.getYear());
            em.merge(book);
            return book;
        }
        return null; // Book not found
    }

    @Transactional
    public boolean deleteBook(Long id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
            return true;
        }
        return false;
    }
}