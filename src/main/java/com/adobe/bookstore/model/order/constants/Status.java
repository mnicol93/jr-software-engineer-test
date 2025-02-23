package com.adobe.bookstore.model.order.constants;

public enum Status {
  Success,  // Order placed
  Pending,  // Checking stock availability
  Rejected  // No stock available
}
