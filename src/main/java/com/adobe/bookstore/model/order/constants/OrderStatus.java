package com.adobe.bookstore.model.order.constants;

/**
 * Enum representing the status of an order.
 */
public enum OrderStatus {
  Success,  // Order placed
  Pending,  // Checking stock availability
  Rejected  // No stock available
}