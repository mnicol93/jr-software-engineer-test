package com.adobe.bookstore.controller;

import com.adobe.bookstore.dto.OrderRequestDTO;
import com.adobe.bookstore.dto.OrderResponseDTO;
import com.adobe.bookstore.model.order.Order;
import com.adobe.bookstore.service.OrderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling requests related to orders. Provides endpoints for
 * retrieving and managing orders in the system.
 */
@RestController
@RequestMapping("/orders")
public class OrderResource {

  @Autowired
  private OrderService orderService;

  /**
   * Retrieves all orders stored in the database and returns a {@code 200 OK} status code.
   *
   * @return A list of orders. If no orders exist, returns an empty list ( [] ).
   */
  @GetMapping
  public ResponseEntity<List<Order>> getAllOrders() {
    return ResponseEntity.ok(orderService.getOrders());
  }

  /**
   * Creates a new order based on the provided request.
   *
   * @param orderRequest The order request data.
   * @return The response containing the created order details.
   */
  @PostMapping
  public ResponseEntity<OrderResponseDTO> saveOrder(@RequestBody OrderRequestDTO orderRequest) {
    return ResponseEntity.ok(orderService.createOrder(orderRequest));
  }
}
