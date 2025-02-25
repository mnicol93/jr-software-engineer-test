package com.adobe.bookstore.service;

import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.repository.BookStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    bookStock.setId("testBook1");
    bookStock.setQuantity(100);
  }

  @Test
  void testDecreaseStock_WhenBookExists_decreaseFromQuantity() {
    when(bookStockRepository.findById("testBook1")).thenReturn(Optional.of(bookStock));

    bookStockService.decreaseStock("testBook1", 10);

    assertEquals(90, bookStock.getQuantity());
    verify(bookStockRepository).save(bookStock);
  }

  @Test
  void testDecreaseStock_WhenZeroQtyChange_decreaseFromQuantity() {
    assertEquals(
        ResponseEntity.badRequest().build(),
        bookStockService.decreaseStock("testBook1", 0));
  }

  @Test
  void testDecreaseStock_WhenQtyExceedsStock_DoesNotSave() {

    when(bookStockRepository.findById("testBook1")).thenReturn(Optional.of(bookStock));

    ResponseEntity<Void> response = bookStockService.decreaseStock("testBook1", 150);

    // Verify HTTP 422 (Unprocessable Entity) is returned
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    // Verify stock quantity remains unchanged
    assertEquals(100, bookStock.getQuantity());
    // Verify save() was NOT called
    verify(bookStockRepository, never()).save(any());
  }

}