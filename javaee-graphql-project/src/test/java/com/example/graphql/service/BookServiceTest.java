package com.example.graphql.service;

import com.example.graphql.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBooks() {
        Book book1 = new Book(1L, "Book 1", "Author 1", 2020);
        Book book2 = new Book(2L, "Book 2", "Author 2", 2021);
        List<Book> expectedBooks = Arrays.asList(book1, book2);

        TypedQuery<Book> mockQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Book.class))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedBooks);

        List<Book> actualBooks = bookService.getAllBooks();

        assertEquals(expectedBooks, actualBooks);
        verify(entityManager).createQuery("SELECT b FROM Book b", Book.class);
        verify(mockQuery).getResultList();
    }

    @Test
    void testGetAllBooks_Exception() {
        when(entityManager.createQuery(anyString(), eq(Book.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> bookService.getAllBooks());
    }

    @Test
    void testGetBookById_ExistingBook() {
        Book expectedBook = new Book(1L, "Book 1", "Author 1", 2020);
        when(entityManager.find(Book.class, 1L)).thenReturn(expectedBook);

        Optional<Book> actualBook = bookService.getBookById(1L);

        assertTrue(actualBook.isPresent());
        assertEquals(expectedBook, actualBook.get());
        verify(entityManager).find(Book.class, 1L);
    }

    @Test
    void testGetBookById_NonExistingBook() {
        when(entityManager.find(Book.class, 1L)).thenReturn(null);

        Optional<Book> actualBook = bookService.getBookById(1L);

        assertFalse(actualBook.isPresent());
        verify(entityManager).find(Book.class, 1L);
    }

    @Test
    void testGetBookById_NullId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.getBookById(null));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testGetBookById_InvalidId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.getBookById(-1L));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testGetBooksByAuthor() {
        Book book1 = new Book(1L, "Book 1", "Author 1", 2020);
        List<Book> expectedBooks = Arrays.asList(book1);

        TypedQuery<Book> mockQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Book.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("author"), anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedBooks);

        List<Book> actualBooks = bookService.getBooksByAuthor("Author 1");

        assertEquals(expectedBooks, actualBooks);
        verify(entityManager).createQuery("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(:author)", Book.class);
        verify(mockQuery).setParameter("author", "%Author 1%");
        verify(mockQuery).getResultList();
    }

    @Test
    void testGetBooksByAuthor_EmptyAuthor() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.getBooksByAuthor(""));
        assertEquals("Author name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetBooksByAuthor_NullAuthor() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.getBooksByAuthor(null));
        assertEquals("Author name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAddBook() {
        Book book = new Book("Test Book", "Test Author", 2023);

        Book savedBook = bookService.addBook(book);

        assertEquals(book, savedBook);
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(entityManager).persist(bookCaptor.capture());
        assertEquals(book, bookCaptor.getValue());
        verify(entityManager).flush();
    }

    @Test
    void testAddBook_NullBook() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(null));
        assertEquals("Book cannot be null", exception.getMessage());
    }

    @Test
    void testAddBook_NullTitle() {
        Book book = new Book(null, "Test Author", 2023);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(book));
        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAddBook_EmptyTitle() {
        Book book = new Book("", "Test Author", 2023);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(book));
        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAddBook_NullAuthor() {
        Book book = new Book("Test Title", null, 2023);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(book));
        assertEquals("Book author cannot be null or empty", exception.getMessage());
    }

    @Test
    void testUpdateBook() {
        Book existingBook = new Book(1L, "Old Title", "Old Author", 2020);
        Book updatedBook = new Book("New Title", "New Author", 2023);
        when(entityManager.find(Book.class, 1L)).thenReturn(existingBook);

        Book result = bookService.updateBook(1L, updatedBook);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        assertEquals(2023, result.getYear());
        verify(entityManager).find(Book.class, 1L);
        verify(entityManager).merge(existingBook);
    }

    @Test
    void testUpdateBook_NonExistingBook() {
        Book updatedBook = new Book("New Title", "New Author", 2023);
        when(entityManager.find(Book.class, 1L)).thenReturn(null);

        Book result = bookService.updateBook(1L, updatedBook);

        assertNull(result);
        verify(entityManager).find(Book.class, 1L);
    }

    @Test
    void testUpdateBook_NullId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook(null, new Book()));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testUpdateBook_InvalidId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook(-1L, new Book()));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testUpdateBook_NullBook() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook(1L, null));
        assertEquals("Updated book cannot be null", exception.getMessage());
    }

    @Test
    void testDeleteBook_ExistingBook() {
        Book existingBook = new Book(1L, "Test Book", "Test Author", 2023);
        when(entityManager.find(Book.class, 1L)).thenReturn(existingBook);

        boolean result = bookService.deleteBook(1L);

        assertTrue(result);
        verify(entityManager).find(Book.class, 1L);
        verify(entityManager).remove(existingBook);
    }

    @Test
    void testDeleteBook_NonExistingBook() {
        when(entityManager.find(Book.class, 1L)).thenReturn(null);

        boolean result = bookService.deleteBook(1L);

        assertFalse(result);
        verify(entityManager).find(Book.class, 1L);
    }

    @Test
    void testDeleteBook_NullId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.deleteBook(null));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testDeleteBook_InvalidId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.deleteBook(-1L));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }
}