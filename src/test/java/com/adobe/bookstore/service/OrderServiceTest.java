package com.adobe.bookstore.service;

import com.adobe.bookstore.controller.BookStockResource;
import com.adobe.bookstore.dto.OrderRequestDTO;
import com.adobe.bookstore.dto.OrderResponseDTO;
import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.model.order.Order;
import com.adobe.bookstore.model.order.OrderDetail;
import com.adobe.bookstore.model.order.constants.OrderDetailStatus;
import com.adobe.bookstore.model.order.constants.OrderStatus;
import com.adobe.bookstore.repository.OrderRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.hamcrest.Matchers.empty;


class OrderServiceTest {

  @InjectMocks
  private OrderService orderService;
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private BookStockResource bookStockResource;

  private OrderRequestDTO orderRequest;
  private BookStock bookStock;
  private OrderDetail orderDetail;

  @BeforeEach
  void setUp() {
    openMocks(this);

    bookStock = new BookStock();
    bookStock.setId("book-1");
    bookStock.setQuantity(100);

    orderDetail = new OrderDetail();
    orderDetail.setBookId("book-1");
    orderDetail.setQuantity(10);

    List<OrderDetail> orderDetails = List.of(orderDetail);
    orderRequest = new OrderRequestDTO(orderDetails);
  }

  @Test
  void testGetOrders_WhenListIsEmpty_ReturnEmptyList() {
    when(orderRepository.findAll()).thenReturn(Collections.emptyList());
    assertEquals(orderService.getOrders(), Collections.emptyList());
    assertThat(orderService.getOrders(), empty());
  }

  @Test
  void testGetOrders_WhenListIsPopulated_ThenReturnPopulatedList() {
    Order order = new Order();
    order.setOrderDetails(List.of(orderDetail));

    when(orderRepository.findAll()).thenReturn(List.of(order));

    assertEquals(List.of(order), orderRepository.findAll());
  }

  @Test
  void testCreateOrder_WhenOrderDetailStatusNotInStock_ThenRejectOrderStatus() {
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setId("test1");
    orderDetail.setBookId("test11");
    orderDetail.setQuantity(2);

    OrderRequestDTO orderRequest = new OrderRequestDTO(List.of(orderDetail));

    when(bookStockResource.getStockById(any(String.class)))
        .thenReturn(ResponseEntity.of(Optional.empty())); // Simulate no stock
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    OrderResponseDTO response = orderService.createOrder(orderRequest);

    assertEquals(
        OrderDetailStatus.No_Stock,
        orderRequest.getOrderDetails().get(0).getStatus()
    );
  }

  @Test
  void testCreateOrder_WhenOrderIsValid_ThenReturnSuccess() {
    when(bookStockResource.getStockById("book-1")).thenReturn(ResponseEntity.ok(bookStock));

    Order order = new Order();
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    OrderResponseDTO response = orderService.createOrder(orderRequest);

    assertNotNull(response.getId());
    assertEquals(order.getId(), response.getId());
  }

//  @Test
//  void testCreateOrder_WhenStockNotEnough_ThenRejectOrder() {
//    Order order = new Order();
//    OrderResponseDTO orderResponse = new OrderResponseDTO(order.getId());
//
//    when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(orderResponse);
//
//  }


  @Test
  void testCreateOrder_BookNotFound() {
    when(bookStockResource.getStockById("book-1")).thenReturn(ResponseEntity.notFound().build());

    Order rejectedOrder = new Order();
    rejectedOrder.setId(UUID.randomUUID()); // Prevent null pointer
    rejectedOrder.setOrderStatus(OrderStatus.Rejected);
    when(orderRepository.save(any(Order.class))).thenReturn(rejectedOrder);

    OrderResponseDTO response = orderService.createOrder(orderRequest);

    assertNotNull(response.getId(), "Order ID should not be null even if rejected");
    verify(orderRepository, times(1)).save(any(Order.class));
  }

  @Test
  void testUpdateStock() {
    when(bookStockResource.getStockById("book-1")).thenReturn(ResponseEntity.ok(bookStock));

    List<OrderDetail> orderDetails = new ArrayList<>();
    orderDetails.add(orderDetail);
    orderService.updateStock(orderDetails);

    assertEquals(90, bookStock.getQuantity()); // 100 - 10
    verify(bookStockResource, times(1)).getStockById("book-1");
  }

  @Test
  void testCreateOrder_MultipleValidDetails_AcceptOrder() {
    OrderDetail detail2 = new OrderDetail();
    detail2.setBookId("book-2");
    detail2.setQuantity(20);
    BookStock book2Stock = new BookStock();
    book2Stock.setId("book-2");
    book2Stock.setQuantity(30);

    when(bookStockResource.getStockById("book-1")).thenReturn(ResponseEntity.ok(bookStock));
    when(bookStockResource.getStockById("book-2")).thenReturn(ResponseEntity.ok(book2Stock));
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    OrderRequestDTO multiRequest = new OrderRequestDTO(List.of(orderDetail, detail2));

    OrderResponseDTO response = orderService.createOrder(multiRequest);

    assertNotNull(response.getId());
    verify(orderRepository).save(argThat(order -> order.getOrderStatus() == OrderStatus.Success));
  }

  @Test
  void testCreateOrder_ExactStockMatch_AcceptOrder() {
    bookStock.setQuantity(10);
    orderDetail.setQuantity(10);
    when(bookStockResource.getStockById("book-1")).thenReturn(ResponseEntity.ok(bookStock));
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    orderService.createOrder(orderRequest);

    verify(orderRepository).save(argThat(order ->
        order.getOrderStatus() == OrderStatus.Success &&
            order.getOrderDetails().get(0).getStatus() == OrderDetailStatus.In_Stock
    ));
  }

  @Test
  void testCreateOrder_ZeroQuantity_ProcessAsValid() {
    orderDetail.setQuantity(0);
    when(bookStockResource.getStockById("book-1")).thenReturn(ResponseEntity.ok(bookStock));
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    orderService.createOrder(orderRequest);

    verify(orderRepository).save(argThat(order ->
        order.getOrderStatus() == OrderStatus.Success &&
            order.getOrderDetails().get(0).getStatus() == OrderDetailStatus.In_Stock
    ));
  }

  @Test
  void testCreateOrder_NegativeQuantity_ProcessAsValid() {
    orderDetail.setQuantity(-5);
    when(bookStockResource.getStockById("book-1")).thenReturn(ResponseEntity.ok(bookStock));
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    orderService.createOrder(orderRequest);

    verify(orderRepository).save(argThat(order ->
        order.getOrderStatus() == OrderStatus.Success &&
            order.getOrderDetails().get(0).getStatus() == OrderDetailStatus.In_Stock
    ));
  }

  @Test
  void testCreateOrder_MixedValidity_RejectOrder() {
    OrderDetail validDetail = new OrderDetail();
    validDetail.setBookId("book-1");
    validDetail.setQuantity(5);

    OrderDetail invalidDetail = new OrderDetail();
    invalidDetail.setBookId("book-2");
    invalidDetail.setQuantity(20);

    when(bookStockResource.getStockById("book-1")).thenReturn(ResponseEntity.ok(bookStock));
    when(bookStockResource.getStockById("book-2")).thenReturn(ResponseEntity.of(Optional.empty()));
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    OrderRequestDTO mixedRequest = new OrderRequestDTO(List.of(validDetail, invalidDetail));

    orderService.createOrder(mixedRequest);

    verify(orderRepository).save(argThat(order ->
        order.getOrderStatus() == OrderStatus.Rejected &&
            order.getOrderDetails().stream()
                .anyMatch(d -> d.getStatus() == OrderDetailStatus.No_Stock)
    ));
  }

  @Test
  void testUpdateStock_MultipleBooks_UpdateAll() {
    OrderDetail detail2 = new OrderDetail();
    detail2.setBookId("book-2");
    detail2.setQuantity(5);
    BookStock book2Stock = new BookStock();
    book2Stock.setId("book-2");
    book2Stock.setQuantity(15);

    when(bookStockResource.getStockById("book-1")).thenReturn(ResponseEntity.ok(bookStock));
    when(bookStockResource.getStockById("book-2")).thenReturn(ResponseEntity.ok(book2Stock));

    orderService.updateStock(List.of(orderDetail, detail2));

    assertEquals(90, bookStock.getQuantity());
    assertEquals(10, book2Stock.getQuantity());
  }

  @Test
  void testCreateOrder_EmptyDetails_SuccessWithNoItems() {
    OrderRequestDTO emptyRequest = new OrderRequestDTO(Collections.emptyList());
    when(orderRepository.save(any())).thenReturn(new Order());

    orderService.createOrder(emptyRequest);

    verify(orderRepository).save(argThat(order ->
        order.getOrderStatus() == OrderStatus.Success &&
            order.getOrderDetails().isEmpty()
    ));
  }
}
