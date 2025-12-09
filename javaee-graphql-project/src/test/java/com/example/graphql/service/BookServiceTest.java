package com.example.graphql.service;

import com.example.graphql.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private EntityManager entityManager;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
        bookService.em = entityManager;
    }

    @Test
    void testGetAllBooks() {
        // Arrange
        Book book1 = new Book(1L, "Book 1", "Author 1", 2020);
        Book book2 = new Book(2L, "Book 2", "Author 2", 2021);
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        
        TypedQuery<Book> mockQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Book.class))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedBooks);

        // Act
        List<Book> actualBooks = bookService.getAllBooks();

        // Assert
        assertEquals(expectedBooks, actualBooks);
        verify(entityManager).createQuery("SELECT b FROM Book b", Book.class);
        verify(mockQuery).getResultList();
    }

    @Test
    void testGetAllBooks_Exception() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Book.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookService.getAllBooks());
    }

    @Test
    void testGetBookById_ExistingBook() {
        // Arrange
        Book expectedBook = new Book(1L, "Book 1", "Author 1", 2020);
        when(entityManager.find(Book.class, 1L)).thenReturn(expectedBook);

        // Act
        Optional<Book> actualBook = bookService.getBookById(1L);

        // Assert
        assertTrue(actualBook.isPresent());
        assertEquals(expectedBook, actualBook.get());
        verify(entityManager).find(Book.class, 1L);
    }

    @Test
    void testGetBookById_NonExistingBook() {
        // Arrange
        when(entityManager.find(Book.class, 1L)).thenReturn(null);

        // Act
        Optional<Book> actualBook = bookService.getBookById(1L);

        // Assert
        assertFalse(actualBook.isPresent());
        verify(entityManager).find(Book.class, 1L);
    }

    @Test
    void testGetBookById_NullId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.getBookById(null));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testGetBookById_InvalidId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.getBookById(-1L));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testGetBooksByAuthor() {
        // Arrange
        Book book1 = new Book(1L, "Book 1", "Author 1", 2020);
        List<Book> expectedBooks = Arrays.asList(book1);
        
        TypedQuery<Book> mockQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Book.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("author"), anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedBooks);

        // Act
        List<Book> actualBooks = bookService.getBooksByAuthor("Author 1");

        // Assert
        assertEquals(expectedBooks, actualBooks);
        verify(entityManager).createQuery("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(:author)", Book.class);
        verify(mockQuery).setParameter("author", "%Author 1%");
        verify(mockQuery).getResultList();
    }

    @Test
    void testGetBooksByAuthor_EmptyAuthor() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.getBooksByAuthor(""));
        assertEquals("Author name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetBooksByAuthor_NullAuthor() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.getBooksByAuthor(null));
        assertEquals("Author name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAddBook() {
        // Arrange
        Book book = new Book("Test Book", "Test Author", 2023);
        when(entityManager.createQuery(anyString(), eq(Book.class))).thenReturn(mock(TypedQuery.class));

        // Act
        Book savedBook = bookService.addBook(book);

        // Assert
        assertEquals(book, savedBook);
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(entityManager).persist(bookCaptor.capture());
        assertEquals(book, bookCaptor.getValue());
        verify(entityManager).flush();
    }

    @Test
    void testAddBook_NullBook() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.addBook(null));
        assertEquals("Book cannot be null", exception.getMessage());
    }

    @Test
    void testAddBook_NullTitle() {
        // Arrange
        Book book = new Book(null, "Test Author", 2023);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.addBook(book));
        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAddBook_EmptyTitle() {
        // Arrange
        Book book = new Book("", "Test Author", 2023);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.addBook(book));
        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAddBook_NullAuthor() {
        // Arrange
        Book book = new Book("Test Title", null, 2023);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.addBook(book));
        assertEquals("Book author cannot be null or empty", exception.getMessage());
    }

    @Test
    void testUpdateBook() {
        // Arrange
        Book existingBook = new Book(1L, "Old Title", "Old Author", 2020);
        Book updatedBook = new Book("New Title", "New Author", 2023);
        when(entityManager.find(Book.class, 1L)).thenReturn(existingBook);

        // Act
        Book result = bookService.updateBook(1L, updatedBook);

        // Assert
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        assertEquals(2023, result.getYear());
        verify(entityManager).find(Book.class, 1L);
        verify(entityManager).merge(existingBook);
    }

    @Test
    void testUpdateBook_NonExistingBook() {
        // Arrange
        Book updatedBook = new Book("New Title", "New Author", 2023);
        when(entityManager.find(Book.class, 1L)).thenReturn(null);

        // Act
        Book result = bookService.updateBook(1L, updatedBook);

        // Assert
        assertNull(result);
        verify(entityManager).find(Book.class, 1L);
    }

    @Test
    void testUpdateBook_NullId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.updateBook(null, new Book()));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testUpdateBook_InvalidId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.updateBook(-1L, new Book()));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testUpdateBook_NullBook() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.updateBook(1L, null));
        assertEquals("Updated book cannot be null", exception.getMessage());
    }

    @Test
    void testDeleteBook_ExistingBook() {
        // Arrange
        Book existingBook = new Book(1L, "Test Book", "Test Author", 2023);
        when(entityManager.find(Book.class, 1L)).thenReturn(existingBook);

        // Act
        boolean result = bookService.deleteBook(1L);

        // Assert
        assertTrue(result);
        verify(entityManager).find(Book.class, 1L);
        verify(entityManager).remove(existingBook);
    }

    @Test
    void testDeleteBook_NonExistingBook() {
        // Arrange
        when(entityManager.find(Book.class, 1L)).thenReturn(null);

        // Act
        boolean result = bookService.deleteBook(1L);

        // Assert
        assertFalse(result);
        verify(entityManager).find(Book.class, 1L);
    }

    @Test
    void testDeleteBook_NullId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.deleteBook(null));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testDeleteBook_InvalidId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.deleteBook(-1L));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }
}