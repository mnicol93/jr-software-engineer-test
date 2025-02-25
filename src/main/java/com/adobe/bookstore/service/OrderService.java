package com.adobe.bookstore.service;

import com.adobe.bookstore.dto.OrderRequestDTO;
import com.adobe.bookstore.dto.OrderResponseDTO;
import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.model.order.Order;
import com.adobe.bookstore.model.order.OrderDetail;
import com.adobe.bookstore.model.order.constants.OrderDetailStatus;
import com.adobe.bookstore.model.order.constants.OrderStatus;
import com.adobe.bookstore.repository.OrderRepository;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service class for managing orders. Provides methods for handling business logic related to
 * orders.
 */
@Service
public class OrderService {

  Logger LOGGER = LogManager.getLogger();

  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private BookStockService bookStockService;

  /**
   * Fetches all orders from the repository.
   *
   * @return a list of all {@link Order} objects from the database.
   */
  public List<Order> getOrders() {
    return orderRepository.findAll();
  }

  /**
   * Creates a new order based on the provided request data.
   *
   * @param orderRequest The request data for the new order.
   * @return The response containing the created order's ID.
   */
  public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
    Order order = new Order();
    List<OrderDetail> orderDetails = orderRequest.getOrderDetails();

    // Check for sufficient stock
    for (OrderDetail orderDetail : orderDetails) {
      OrderDetailStatus validatedOrder = validateStock(
          orderDetail,
          bookStockService.getStockById(orderDetail.getBookId()).getBody());

      if (validatedOrder != OrderDetailStatus.In_Stock) {
        order.setOrderStatus(OrderStatus.Rejected);
      }
      orderDetail.setStatus(validatedOrder);
      orderDetail.setOrderId(order.getId());
    }

    order.setOrderDetails(orderDetails);
    // Order was succesfully validated, persist difference of stock in DB
    if (order.getOrderStatus() != OrderStatus.Rejected) {
      // call updateStock async
      updateStock(orderDetails);
      order.setOrderStatus(OrderStatus.Success);
    }

    Order savedOrder = orderRepository.save(order);

    return new OrderResponseDTO(savedOrder.getId());
  }

  /**
   * Asynchronously updates stock quantities after an order is placed.
   *
   * @param orderDetails The details of the order to process.
   */
  @Async          // Updating stock won't block the response
  public void updateStock(List<OrderDetail> orderDetails) {
    try {
      for (OrderDetail orderDetail : orderDetails) {
        BookStock book = bookStockService.getStockById(orderDetail.getBookId()).getBody();

        if (book != null) {
          bookStockService.decreaseFromStock(orderDetail.getBookId(), orderDetail.getQuantity());
        }
      }
    } catch (RuntimeException e) {
      LOGGER.error("Error {}", e);
    }
  }

  private OrderDetailStatus validateStock(OrderDetail orderDetail, BookStock book) {
    if (book == null) {
      LOGGER.error("Book {} not found. Order is discarded", orderDetail.getBookId());
      return OrderDetailStatus.No_Stock;
    } else if (orderDetail.getQuantity() < 1) {
      return OrderDetailStatus.Invalid_Amount_Requested;
    } else if (book.getQuantity() - orderDetail.getQuantity() >= 0) {
      return OrderDetailStatus.In_Stock;
    } else {
      LOGGER.warn(
          "Order will be rejected, insufficient stock for book {}",
          orderDetail.getBookId());
      // We could stop here, but continue to keep track of user's intended purchases
      return OrderDetailStatus.Not_Enough_Stock;
    }
  }
}
