package com.adobe.bookstore.dto;

import com.adobe.bookstore.model.order.OrderDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

/**
 * Data Transfer Object representing an order request.
 */
@Getter
public class OrderRequestDTO {

  private final List<OrderDetail> orderDetails;

  /**
   * Constructs an OrderRequestDTO.
   *
   * @param orderDetails The details of the order.
   */
  public OrderRequestDTO(@JsonProperty("orderDetails") List<OrderDetail> orderDetails) {
    this.orderDetails = orderDetails;
  }
}
