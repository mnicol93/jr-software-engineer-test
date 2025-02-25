package com.adobe.bookstore.service;

import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.repository.BookStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookStockServiceTest {

  @Mock
  private BookStockRepository bookStockRepository;

  @InjectMocks
  private BookStockService bookStockService;

  private BookStock bookStock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    bookStock = new BookStock();
    bookStock.setId("book-1");
    bookStock.setQuantity(100);
  }

  @Test
  void testUpdateStock_WhenBookExists_UpdateQuantity() {
    when(bookStockRepository.findById("book-1")).thenReturn(Optional.of(bookStock));

    bookStockService.updateStock("book-1", -10);

    assertEquals(90, bookStock.getQuantity());
    verify(bookStockRepository).save(bookStock);
  }

  @Test
  void testUpdateStock_WhenBookNotFound_ThrowException() {
    when(bookStockRepository.findById("book-1")).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class,
        () -> bookStockService.updateStock("book-1", 10),
        "Book not found"
    );
    verify(bookStockRepository, never()).save(any());
  }

  @Test
  void testUpdateStock_WhenZeroQtyChange_UpdateQuantity() {
    when(bookStockRepository.findById("book-1")).thenReturn(Optional.of(bookStock));

    bookStockService.updateStock("book-1", 0);

    assertEquals(100, bookStock.getQuantity());
    verify(bookStockRepository).save(bookStock);
  }

  @Test
  void testUpdateStock_WhenPositiveQtyChange_UpdateQuantity() {
    when(bookStockRepository.findById("book-1")).thenReturn(Optional.of(bookStock));

    bookStockService.updateStock("book-1", 50);

    assertEquals(150, bookStock.getQuantity());
    verify(bookStockRepository).save(bookStock);
  }

  @Test
  void testUpdateStock_WhenNegativeQtyExceedsStock_UpdateQuantity() {
    when(bookStockRepository.findById("book-1")).thenReturn(Optional.of(bookStock));

    bookStockService.updateStock("book-1", -150);

    assertEquals(-50, bookStock.getQuantity());
    verify(bookStockRepository).save(bookStock);
  }
}