package com.example.graphql.api;

import com.example.graphql.BookGraphQLApi;
import com.example.graphql.model.Book;
import com.example.graphql.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookGraphQLApiTest {

    @Mock
    private BookService bookService;

    private BookGraphQLApi bookGraphQLApi;

    @BeforeEach
    void setUp() {
        bookGraphQLApi = new BookGraphQLApi();
        bookGraphQLApi.bookService = bookService;
    }

    @Test
    void testGetAllBooks() {
        // Arrange
        Book book1 = new Book(1L, "Book 1", "Author 1", 2020);
        Book book2 = new Book(2L, "Book 2", "Author 2", 2021);
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookService.getAllBooks()).thenReturn(expectedBooks);

        // Act
        List<Book> actualBooks = bookGraphQLApi.getAllBooks();

        // Assert
        assertEquals(expectedBooks, actualBooks);
        verify(bookService).getAllBooks();
    }

    @Test
    void testGetBook_ExistingBook() {
        // Arrange
        Book expectedBook = new Book(1L, "Book 1", "Author 1", 2020);
        when(bookService.getBookById(1L)).thenReturn(Optional.of(expectedBook));

        // Act
        Book actualBook = bookGraphQLApi.getBook(1L);

        // Assert
        assertEquals(expectedBook, actualBook);
        verify(bookService).getBookById(1L);
    }

    @Test
    void testGetBook_NonExistingBook() {
        // Arrange
        when(bookService.getBookById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookGraphQLApi.BookNotFoundException.class, () -> bookGraphQLApi.getBook(1L));
        verify(bookService).getBookById(1L);
    }

    @Test
    void testGetBooksByAuthor() {
        // Arrange
        Book book1 = new Book(1L, "Book 1", "Author 1", 2020);
        List<Book> expectedBooks = Arrays.asList(book1);
        when(bookService.getBooksByAuthor("Author 1")).thenReturn(expectedBooks);

        // Act
        List<Book> actualBooks = bookGraphQLApi.getBooksByAuthor("Author 1");

        // Assert
        assertEquals(expectedBooks, actualBooks);
        verify(bookService).getBooksByAuthor("Author 1");
    }

    @Test
    void testAddBook() {
        // Arrange
        BookGraphQLApi.BookInput input = new BookGraphQLApi.BookInput();
        input.setTitle("Test Book");
        input.setAuthor("Test Author");
        input.setYear(2023);
        
        Book expectedBook = new Book("Test Book", "Test Author", 2023);
        when(bookService.addBook(any(Book.class))).thenReturn(expectedBook);

        // Act
        Book actualBook = bookGraphQLApi.addBook(input);

        // Assert
        assertEquals(expectedBook, actualBook);
        verify(bookService).addBook(any(Book.class));
    }

    @Test
    void testAddBook_InvalidInput_Null() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.addBook(null));
        assertEquals("Book input cannot be null", exception.getMessage());
    }

    @Test
    void testAddBook_InvalidInput_NullTitle() {
        // Arrange
        BookGraphQLApi.BookInput input = new BookGraphQLApi.BookInput();
        input.setTitle(null);
        input.setAuthor("Test Author");
        input.setYear(2023);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.addBook(input));
        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAddBook_InvalidInput_EmptyTitle() {
        // Arrange
        BookGraphQLApi.BookInput input = new BookGraphQLApi.BookInput();
        input.setTitle("");
        input.setAuthor("Test Author");
        input.setYear(2023);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.addBook(input));
        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAddBook_InvalidInput_NullAuthor() {
        // Arrange
        BookGraphQLApi.BookInput input = new BookGraphQLApi.BookInput();
        input.setTitle("Test Title");
        input.setAuthor(null);
        input.setYear(2023);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.addBook(input));
        assertEquals("Book author cannot be null or empty", exception.getMessage());
    }

    @Test
    void testAddBook_InvalidInput_NegativeYear() {
        // Arrange
        BookGraphQLApi.BookInput input = new BookGraphQLApi.BookInput();
        input.setTitle("Test Title");
        input.setAuthor("Test Author");
        input.setYear(-1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.addBook(input));
        assertEquals("Book year cannot be negative", exception.getMessage());
    }

    @Test
    void testUpdateBook() {
        // Arrange
        BookGraphQLApi.BookInput input = new BookGraphQLApi.BookInput();
        input.setTitle("Updated Title");
        input.setAuthor("Updated Author");
        input.setYear(2023);
        
        Book updatedBook = new Book(1L, "Updated Title", "Updated Author", 2023);
        when(bookService.updateBook(1L, any(Book.class))).thenReturn(updatedBook);

        // Act
        Book actualBook = bookGraphQLApi.updateBook(1L, input);

        // Assert
        assertEquals(updatedBook, actualBook);
        verify(bookService).updateBook(1L, any(Book.class));
    }

    @Test
    void testUpdateBook_InvalidId() {
        // Arrange
        BookGraphQLApi.BookInput input = new BookGraphQLApi.BookInput();
        input.setTitle("Updated Title");
        input.setAuthor("Updated Author");
        input.setYear(2023);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.updateBook(null, input));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testUpdateBook_InvalidId_Zero() {
        // Arrange
        BookGraphQLApi.BookInput input = new BookGraphQLApi.BookInput();
        input.setTitle("Updated Title");
        input.setAuthor("Updated Author");
        input.setYear(2023);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.updateBook(0L, input));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testUpdateBook_InvalidId_Negative() {
        // Arrange
        BookGraphQLApi.BookInput input = new BookGraphQLApi.BookInput();
        input.setTitle("Updated Title");
        input.setAuthor("Updated Author");
        input.setYear(2023);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.updateBook(-1L, input));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testUpdateBook_InvalidInput_Null() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.updateBook(1L, null));
        assertEquals("Book input cannot be null", exception.getMessage());
    }

    @Test
    void testUpdateBook_NonExistingBook() {
        // Arrange
        BookGraphQLApi.BookInput input = new BookGraphQLApi.BookInput();
        input.setTitle("Updated Title");
        input.setAuthor("Updated Author");
        input.setYear(2023);
        when(bookService.updateBook(1L, any(Book.class))).thenReturn(null);

        // Act & Assert
        assertThrows(BookGraphQLApi.BookNotFoundException.class, 
            () -> bookGraphQLApi.updateBook(1L, input));
        verify(bookService).updateBook(1L, any(Book.class));
    }

    @Test
    void testDeleteBook() {
        // Arrange
        when(bookService.deleteBook(1L)).thenReturn(true);

        // Act
        boolean result = bookGraphQLApi.deleteBook(1L);

        // Assert
        assertTrue(result);
        verify(bookService).deleteBook(1L);
    }

    @Test
    void testDeleteBook_NonExistingBook() {
        // Arrange
        when(bookService.deleteBook(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(BookGraphQLApi.BookNotFoundException.class, 
            () -> bookGraphQLApi.deleteBook(1L));
        verify(bookService).deleteBook(1L);
    }

    @Test
    void testDeleteBook_InvalidId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.deleteBook(null));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testDeleteBook_InvalidId_Zero() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.deleteBook(0L));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }

    @Test
    void testDeleteBook_InvalidId_Negative() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookGraphQLApi.deleteBook(-1L));
        assertEquals("Book ID must be a positive number", exception.getMessage());
    }
}