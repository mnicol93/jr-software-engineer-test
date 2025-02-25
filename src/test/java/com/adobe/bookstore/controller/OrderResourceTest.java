package com.adobe.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.adobe.bookstore.dto.OrderRequestDTO;
import com.adobe.bookstore.dto.OrderResponseDTO;
import com.adobe.bookstore.model.order.Order;
import com.adobe.bookstore.service.OrderService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class) // Enable Mockito
public class OrderResourceTest {

  @Mock
  private OrderService orderService;

  @InjectMocks
  private OrderResource orderResource;

  @Test
  void getAllOrders_ReturnsOrders() {
    // Arrange
    Order order = new Order();
    when(orderService.getOrders()).thenReturn(List.of(order));

    // Act
    ResponseEntity<List<Order>> response = orderResource.getAllOrders();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
  }

  @Test
  void saveOrder_ReturnsOrderResponse() {
    // Arrange
    OrderRequestDTO request = new OrderRequestDTO(List.of());
    OrderResponseDTO expectedResponse = new OrderResponseDTO(UUID.randomUUID());
    when(orderService.createOrder(request)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<OrderResponseDTO> response = orderResource.saveOrder(request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResponse, response.getBody());
  }
}