package com.adobe.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

import com.adobe.bookstore.dto.OrderRequestDTO;
import com.adobe.bookstore.dto.OrderResponseDTO;
import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.model.order.Order;
import com.adobe.bookstore.model.order.OrderDetail;
import com.adobe.bookstore.model.order.constants.OrderDetailStatus;
import com.adobe.bookstore.model.order.constants.OrderStatus;
import com.adobe.bookstore.repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

class OrderServiceTest {

  @InjectMocks
  private OrderService orderService;
  @Mock
  private OrderRepository orderRepository;

  @Mock
  private BookStockService bookStockService;

  private BookStock validBookStock;

  @BeforeEach
  void setUp() {
    openMocks(this);
    validBookStock = new BookStock();
    validBookStock.setId("testBook");
    validBookStock.setQuantity(5);
  }

  @Test
  void createOrder_AllItemsValid_ReturnsSuccess() {
    // Arrange
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setBookId("testBook");
    orderDetail.setQuantity(5);
    OrderRequestDTO request = new OrderRequestDTO(List.of(orderDetail));

    when(bookStockService.getStockById("testBook"))
        .thenReturn(ResponseEntity.ok(validBookStock));
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
      Order savedOrder = invocation.getArgument(0);
      savedOrder.setId(UUID.randomUUID()); // Simulate ID generation
      return savedOrder;
    });

    // Act
    OrderResponseDTO response = orderService.createOrder(request);

    // Assert
    ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepository).save(orderCaptor.capture());
    Order savedOrder = orderCaptor.getValue();

    assertEquals(OrderStatus.Success, savedOrder.getOrderStatus());
    assertEquals(OrderDetailStatus.In_Stock, savedOrder.getOrderDetails().get(0).getStatus());
  }

  @Test
  void createOrder_InsufficientStock_ReturnsRejected() {
    Order order = new Order();
    order.setOrderStatus(OrderStatus.Rejected);
    OrderDetail detail = new OrderDetail();
    detail.setBookId("book1");
    detail.setQuantity(15);

    OrderRequestDTO request = new OrderRequestDTO(List.of(detail));

    when(bookStockService.getStockById("book1"))
        .thenReturn(ResponseEntity.ok(validBookStock));
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    orderService.createOrder(request);

    assertEquals(OrderDetailStatus.Not_Enough_Stock, detail.getStatus());
    assertEquals(OrderStatus.Rejected, order.getOrderStatus());
    verify(bookStockService, never()).decreaseFromStock(any(), anyInt());
  }

  @Test
  void updateStock_ValidBook_ReducesStock() {
    // Arrange
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setBookId("testBook");
    orderDetail.setQuantity(3);

    BookStock validBookStock = new BookStock(); // Initial stock: 5
    validBookStock.setId("testBook");
    validBookStock.setQuantity(5);
    // Mock service calls
    when(bookStockService.getStockById("testBook"))
        .thenReturn(ResponseEntity.ok(validBookStock));

    // Simulate stock update in the mock
    when(bookStockService.decreaseFromStock(eq("testBook"), anyInt()))
        .thenAnswer(invocation -> {
          int qtyChange = invocation.getArgument(1);
          validBookStock.setQuantity(validBookStock.getQuantity() - qtyChange);
          return ResponseEntity.ok().build();
        });

    // Act
    orderService.updateStock(List.of(orderDetail));

    // Assert
    assertEquals(2, validBookStock.getQuantity()); // 5 - 3 = 2
  }

}