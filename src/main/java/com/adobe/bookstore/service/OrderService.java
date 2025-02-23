package com.adobe.bookstore.service;

import com.adobe.bookstore.model.order.Order;
import com.adobe.bookstore.repository.OrderRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing orders. Provides methods for handling business logic related to
 * orders.
 */
@Service
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;

  /**
   * Fetches all orders from the repository.
   *
   * @return a list of all {@link Order} objects from the database.
   */
  public List<Order> getOrders() {
    return orderRepository.findAll();
  }
}
