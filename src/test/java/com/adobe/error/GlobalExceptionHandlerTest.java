package com.adobe.error;

import com.adobe.bookstore.BookStockResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

  @Mock
  private GlobalExceptionHandler handler;
  @Mock
  private OrderNotFoundException e;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handleOrderNotFoundException() {
    final String ERROR_MESSAGE = "Order not found";

    when(e.getMessage()).thenReturn(ERROR_MESSAGE);
    when(handler.handleOrderNotFoundException(any(OrderNotFoundException.class)))
        .thenReturn(new ResponseEntity<>(ERROR_MESSAGE, HttpStatus.NOT_FOUND));

    ResponseEntity<String> response = handler.handleOrderNotFoundException(e);

    assertEquals(new ResponseEntity<>(HttpStatus.NOT_FOUND).getStatusCode(),
        response.getStatusCode());
  }
}