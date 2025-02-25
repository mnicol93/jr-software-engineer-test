package com.adobe.bookstore.controller;

import com.adobe.bookstore.dto.StockUpdateDTO;
import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.repository.BookStockRepository;
import com.adobe.bookstore.service.BookStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

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
    openMocks(this);

    bookStock = new BookStock();
    bookStock.setId("testBook1");
    bookStock.setQuantity(100);
  }

  @Test
  void testGetStockById_WhenBookExists_ReturnBookStock() {
    when(bookStockService.getStockById("testBook1"))
        .thenReturn(ResponseEntity.ok(bookStock));

    ResponseEntity<BookStock> response = bookStockResource.getStockById("testBook1");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(bookStock, response.getBody());
    verify(bookStockService).getStockById("testBook1"); // Verify SERVICE interaction
  }

  @Test
  void testGetStockById_WhenBookDoesNotExist_ReturnNotFound() {
    ResponseEntity<BookStock> notFound = ResponseEntity.notFound().build();
    when(bookStockService.getStockById("testBook1"))
        .thenReturn(ResponseEntity.notFound().build());

    ResponseEntity<BookStock> response = bookStockResource.getStockById("testBook1");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(bookStockService).getStockById("testBook1");
  }

  @Test
  void testUpdateStock_WhenBookExists_ReturnOk() {
    StockUpdateDTO stockUpdate = new StockUpdateDTO(10);

    when(bookStockService.decreaseStock("testBook1", 10))
        .thenReturn(ResponseEntity.ok().build());

    ResponseEntity<Void> response = bookStockResource.updateStock("testBook1", stockUpdate);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(bookStockService).decreaseStock("testBook1", 10);
  }

  @Test
  void testUpdateStock_WhenBookDoesNotExist_ReturnNotFound() {
    StockUpdateDTO stockUpdate = new StockUpdateDTO(10);

    when(bookStockService.decreaseStock("testBook1", 10))
        .thenReturn(ResponseEntity.notFound().build());

    ResponseEntity<Void> response = bookStockResource.updateStock("testBook1", stockUpdate);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(bookStockService).decreaseStock("testBook1", 10);
  }
}