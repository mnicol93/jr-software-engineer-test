package com.adobe.bookstore.controller;

import com.adobe.bookstore.dto.StockUpdateDTO;
import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.repository.BookStockRepository;
import com.adobe.bookstore.service.BookStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookStockResourceTest {

  @InjectMocks
  private BookStockResource bookStockResource;

  @Mock
  private BookStockRepository bookStockRepository;

  @Mock
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
  void testGetStockById_WhenBookExists_ReturnBookStock() {
    when(bookStockRepository.findById("book-1")).thenReturn(Optional.of(bookStock));

    ResponseEntity<BookStock> response = bookStockResource.getStockById("book-1");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(bookStock, response.getBody());
    verify(bookStockRepository).findById("book-1");
  }

  @Test
  void testGetStockById_WhenBookDoesNotExist_ReturnNotFound() {
    when(bookStockRepository.findById("book-1")).thenReturn(Optional.empty());

    ResponseEntity<BookStock> response = bookStockResource.getStockById("book-1");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(bookStockRepository).findById("book-1");
  }

  @Test
  void testUpdateStock_WhenBookExists_ReturnOk() {
    StockUpdateDTO stockUpdate = new StockUpdateDTO(10);

    doNothing().when(bookStockService).updateStock("book-1", 10);

    ResponseEntity<Void> response = bookStockResource.updateStock("book-1", stockUpdate);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(bookStockService).updateStock("book-1", 10);
  }

  @Test
  void testUpdateStock_WhenBookDoesNotExist_ReturnNotFound() {
    StockUpdateDTO stockUpdate = new StockUpdateDTO(10);

    doThrow(new RuntimeException("Book not found")).when(bookStockService)
        .updateStock("book-1", 10);

    ResponseEntity<Void> response = bookStockResource.updateStock("book-1", stockUpdate);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(bookStockService).updateStock("book-1", 10);
  }
}